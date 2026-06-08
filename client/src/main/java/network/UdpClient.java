package network;

import jdk.internal.org.jline.utils.Log;
import protocol.CommandRequest;
import protocol.CommandResponse;
import protocol.PacketChunk;
import protocol.SerializationUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;

public class UdpClient {
    private static final int CHUNK_SIZE = 1024;
    private static final int BUFFER_SIZE = 4096; // Немного увеличим буфер для безопасности сериализации чанка

    private final InetSocketAddress serverAddress;
    private final DatagramChannel channel;
    private final ResponseTracker responseTracker;
    private final ExecutorService listenerPool = Executors.newSingleThreadExecutor();

    // ИСПРАВЛЕНИЕ: Карта сборщика вынесена в поле класса, чтобы чанки не стирались между итерациями цикла!
    private final Map<Long, Map<Integer, byte[]>> assemblers = new ConcurrentHashMap<>();
    private Log logger;

    public UdpClient(String host, int port) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.connect(serverAddress);
        this.responseTracker = new ResponseTracker();
        startListener();
    }

    private void startListener() {
        listenerPool.submit(() -> {
            ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    receiveBuffer.clear();
                    if (channel.read(receiveBuffer) > 0) {
                        receiveBuffer.flip();
                        byte[] chunkBytes = new byte[receiveBuffer.remaining()];
                        receiveBuffer.get(chunkBytes);

                        PacketChunk chunk = SerializationUtils.deserialize(chunkBytes, PacketChunk.class);
                        if (chunk == null) {
                            Log.warn("Получен пустой пакет");
                            continue;
                        }
                        long msgId = chunk.messageId();
                        assemblers.putIfAbsent(msgId, new TreeMap<>());
                        Map<Integer, byte[]> chunks = assemblers.get(msgId);

                        synchronized (chunks) {
                            chunks.put(chunk.index(), chunk.data());

                            if (chunks.size() == chunk.total()) {
                                CommandResponse response = assembleResponse(chunks);

                                responseTracker.complete(msgId, response);
                                assemblers.remove(msgId);
                            }
                        }
                    } else {
                        Thread.sleep(5);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    Log.error("Ошибка при сборке пакета: {}", e.toString());
                }
            }
        });
    }

    public CompletableFuture<CommandResponse> send(CommandRequest request) throws IOException {
        long messageId = System.nanoTime();

        CommandRequest enrichedRequest = new CommandRequest(
                request.commandName(),
                request.arguments(),
                request.bandPayload(),
                request.studioPayload(),
                request.login(),
                request.password()
        );

        byte[] fullPayload = SerializationUtils.serialize(enrichedRequest);
        int totalChunks = (int) Math.ceil((double) fullPayload.length / CHUNK_SIZE);

        CompletableFuture<CommandResponse> future = responseTracker.track(messageId);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * CHUNK_SIZE;
            int length = Math.min(fullPayload.length - start, CHUNK_SIZE);
            byte[] portion = new byte[length];
            System.arraycopy(fullPayload, start, portion, 0, length);

            PacketChunk chunk = new PacketChunk(messageId, i, totalChunks, portion);
            byte[] serializedChunk = SerializationUtils.serialize(chunk);
            channel.write(ByteBuffer.wrap(serializedChunk));
        }

        return future;
    }

    private CommandResponse assembleResponse(Map<Integer, byte[]> chunks) throws IOException, ClassNotFoundException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        for (byte[] data : chunks.values()) {
            bos.write(data);
        }
        return SerializationUtils.deserialize(bos.toByteArray(), CommandResponse.class);
    }

    public void close() throws IOException {
        listenerPool.shutdownNow();
        channel.close();
    }
}