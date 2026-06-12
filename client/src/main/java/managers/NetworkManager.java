package managers;

import network.ClientRequestDispatcher;
import network.UdpClient;
import protocol.CommandRequest;
import protocol.CommandResponse;
import structs.MusicBand;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class NetworkManager {
    private static NetworkManager instance;
    private  UdpClient udpClient;
    private  ClientRequestDispatcher dispatcher;
    private Thread listenerThread;
    private volatile boolean running = true;
    private Consumer<String> onMessageReceived;

    private NetworkManager() {
        this.udpClient = null;
        this.dispatcher = null;
    }

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void init(String host, int port) {
        try {
            this.udpClient = new UdpClient(host, port);
            this.dispatcher = new ClientRequestDispatcher(udpClient);
            startListener();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize UDP client: " + e.getMessage());
        }
    }

    private void startListener() {
        listenerThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] buffer = new byte[4096];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());

                    if (onMessageReceived != null) {
                        onMessageReceived.accept(message);
                    }
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }, "UDP-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void setOnMessageReceived(Consumer<String> callback) {
        this.onMessageReceived = callback;
    }

    public CompletableFuture<CommandResponse> sendRequest(CommandRequest request) {
        return udpClient.send(request);
    }

    public CommandResponse sendRequestSync(CommandRequest request) {
        try {
            return sendRequest(request).get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return CommandResponse.error("Network error: " + e.getMessage(), null);
        }
    }

    public CommandResponse sendLogin(String login, String password) {
        CommandRequest request = new CommandRequest("login", login, password, null, null);
        return sendRequestSync(request);
    }

    public CommandResponse sendRegister(String login, String password) {
        CommandRequest request = new CommandRequest("register", login, password, null, null);
        return sendRequestSync(request);
    }

    public CommandResponse sendAdd(MusicBand band) {
        CommandRequest request = new CommandRequest("add", null, null, band, null);
        return sendRequestSync(request);
    }

    public CommandResponse sendUpdate(MusicBand band) {
        CommandRequest request = new CommandRequest("update", null, null, band, null);
        return sendRequestSync(request);
    }

    public CommandResponse sendRemoveById(int id) {
        CommandRequest request = new CommandRequest("removebyid", List.of(String.valueOf(id)), null, null, null);
        return sendRequestSync(request);
    }

    public UdpClient getUdpClient() {
        return udpClient;
    }

    public void stop() {
        running = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }
}
