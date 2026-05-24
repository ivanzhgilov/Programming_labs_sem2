package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.CommandResponse;
import protocol.PacketChunk;
import protocol.SerializationUtils;
import utility.RuntimeConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class UdpResponseSender {
    private static final Logger logger = LoggerFactory.getLogger(UdpResponseSender.class);
    private static int CHUNK_SIZE = 1024;

    public void send(DatagramSocket socket, SocketAddress address, CommandResponse response, long messageId) throws IOException {
        byte[] fullPayload = SerializationUtils.serialize(response);
        int totalChunks = (int) Math.ceil((double) fullPayload.length / CHUNK_SIZE);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * CHUNK_SIZE;
            int length = Math.min(fullPayload.length - start, CHUNK_SIZE);
            byte[] portion = new byte[length];
            System.arraycopy(fullPayload, start, portion, 0, length);
            PacketChunk chunk = new PacketChunk(messageId, i, totalChunks, portion);
            byte[] serializedChunk = SerializationUtils.serialize(chunk);
            DatagramPacket packet = new DatagramPacket(serializedChunk, serializedChunk.length, address);
            socket.send(packet);
        }
        logger.info("Ответ (ID:{}) отправлен на {} (частей: {}, байт: {})",
                messageId, address, totalChunks, fullPayload.length);
    }

    public static void applyRuntimeSettings(RuntimeConfig config) {
        if (config.packetCapacityBytes() >= 0) {
            CHUNK_SIZE = config.packetCapacityBytes();
        }
    }
}