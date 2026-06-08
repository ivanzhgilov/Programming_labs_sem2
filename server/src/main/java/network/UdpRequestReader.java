package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.CommandRequest;
import protocol.PacketChunk;
import protocol.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class UdpRequestReader {
    private static final Logger logger = LoggerFactory.getLogger(UdpRequestReader.class);
    private static final int BUFFER_SIZE = 2048;
    private final Map<String, Map<Integer, byte[]>> assemblers = new ConcurrentHashMap<>();
    public UdpRequestEnvelope read(DatagramSocket socket) throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        byte[] rawPayload = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), 0, rawPayload, 0, packet.getLength());
        PacketChunk chunk = SerializationUtils.deserialize(rawPayload, PacketChunk.class);
        SocketAddress clientAddress = packet.getSocketAddress();
        String assemblyKey = clientAddress.toString() + ":" + chunk.messageId();
        assemblers.putIfAbsent(assemblyKey, new TreeMap<>());
        Map<Integer, byte[]> chunks = assemblers.get(assemblyKey);
        chunks.put(chunk.index(), chunk.data());
        logger.info("Получен чанк {}/{} от {}", chunk.index() + 1, chunk.total(), clientAddress);
        if (chunks.size() == chunk.total()) {
            byte[] completeData = assemble(chunks);
            assemblers.remove(assemblyKey);
            CommandRequest request = SerializationUtils.deserialize(completeData, CommandRequest.class);
            return new UdpRequestEnvelope(clientAddress, request, chunk.messageId());
        }
        return null;
    }

    private byte[] assemble(Map<Integer, byte[]> chunks) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            for (byte[] data : chunks.values()) {
                bos.write(data);
            }
            return bos.toByteArray();
        }
    }
}