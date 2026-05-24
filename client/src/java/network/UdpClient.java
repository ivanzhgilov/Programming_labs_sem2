package network;

import protocol.CommandRequest;
import protocol.CommandResponse;
import exceptions.ErrorLevel;
import protocol.PacketChunk;
import protocol.SerializationUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class UdpClient {
    private static final int CHUNK_SIZE = 1024;
    private static final int BUFFER_SIZE = 2048;
    private static final int TIMEOUT_MS = 2000;
    private static final int RETRIES = 3;

    private final InetSocketAddress serverAddress;

    public UdpClient(String host, int port) {
        this.serverAddress = new InetSocketAddress(host, port);
    }

    public CommandResponse send(CommandRequest request) throws IOException, ClassNotFoundException {
        byte[] fullPayload = SerializationUtils.serialize(request);
        long messageId = System.currentTimeMillis();
        int totalChunks = (int) Math.ceil((double) fullPayload.length / CHUNK_SIZE);
        try (DatagramChannel channel = DatagramChannel.open(); Selector selector = Selector.open()) {
            channel.configureBlocking(false);
            channel.connect(serverAddress);
            channel.register(selector, SelectionKey.OP_READ);

            for (int attempt = 1; attempt <= RETRIES; attempt++) {
                sendAllChunks(channel, messageId, fullPayload, totalChunks);
                CommandResponse response = receiveFullResponse(channel, selector, messageId);
                if (response != null) return response;
            }
        }
        return CommandResponse.error("Сервер недоступен после " + RETRIES + " попыток.", ErrorLevel.SERVER);
    }

    private CommandResponse receiveFullResponse(DatagramChannel channel, Selector selector, long expectedMessageId)
            throws IOException, ClassNotFoundException {
        java.util.Map<Integer, byte[]> receivedChunks = new java.util.TreeMap<>();
        int expectedTotal = -1;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
            if (selector.select(500) > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isReadable()) {
                        ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                        channel.read(receiveBuffer);
                        receiveBuffer.flip();
                        byte[] chunkBytes = new byte[receiveBuffer.remaining()];
                        receiveBuffer.get(chunkBytes);
                        PacketChunk chunk = SerializationUtils.deserialize(chunkBytes, PacketChunk.class);
                        if (chunk.messageId() != expectedMessageId) {
                            continue;
                        }
                        receivedChunks.put(chunk.index(), chunk.data());
                        expectedTotal = chunk.total();
                        if (expectedTotal != -1 && receivedChunks.size() == expectedTotal) {
                            return assembleResponse(receivedChunks);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void sendAllChunks(DatagramChannel channel, long msgId, byte[] data, int total) throws IOException {
        for (int i = 0; i < total; i++) {
            int start = i * CHUNK_SIZE;
            int length = Math.min(data.length - start, CHUNK_SIZE);
            byte[] portion = new byte[length];
            System.arraycopy(data, start, portion, 0, length);

            PacketChunk chunk = new PacketChunk(msgId, i, total, portion);
            byte[] serializedChunk = SerializationUtils.serialize(chunk);
            channel.write(ByteBuffer.wrap(serializedChunk));
        }
    }

    private CommandResponse assembleResponse(java.util.Map<Integer, byte[]> chunks) throws IOException, ClassNotFoundException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        for (byte[] data : chunks.values()) {
            bos.write(data);
        }
        return SerializationUtils.deserialize(bos.toByteArray(), CommandResponse.class);
    }
}
