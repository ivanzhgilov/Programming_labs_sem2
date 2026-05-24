package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.RuntimeConfig;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpConnectionModule {
    private static final Logger logger = LoggerFactory.getLogger(UdpConnectionModule.class);
    private DatagramSocket socket;
    private int boundPort;

    public UdpConnectionModule(int port) throws IOException {
        this.socket = new DatagramSocket(new InetSocketAddress(port));
        this.socket.setSoTimeout(100);
        this.boundPort = port;
        logger.info("Datagram socket bind {}", socket.getLocalSocketAddress());
    }

    public int port() {
        return boundPort;
    }

    public DatagramSocket socket() {
        return socket;
    }

    public void applyRuntimeSettings(RuntimeConfig config) throws IOException {
        if (config.socketTimeoutMs() >= 0) {
            socket.setSoTimeout(config.socketTimeoutMs());
        }
        if (config.socketReceiveBufferBytes() > 0) {
            socket.setReceiveBufferSize(config.socketReceiveBufferBytes());
        }
    }

    public void rebind(int newPort) throws IOException {
        if (newPort == boundPort) {
            return;
        }
        logger.info("Перепривязка UDP сокета: {} -> {}", boundPort, newPort);
        socket.close();
        socket = new DatagramSocket(new InetSocketAddress(newPort));
        boundPort = newPort;
        logger.info("Datagram socket rebind {}", socket.getLocalSocketAddress());
    }
}
