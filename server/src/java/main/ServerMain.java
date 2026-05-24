package main;

import exceptions.PhysicalException;
import network.UdpConnectionModule;
import network.UdpRequestDispatcher;
import network.UdpRequestProcessor;
import network.UdpRequestReader;
import network.UdpResponseSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.ConfigLoader;
import utility.RuntimeConfig;
import utility.loader.Loader;

import java.io.IOException;
import java.net.SocketTimeoutException;

public final class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

    private ServerMain() {
    }


    public static void run(String[] args) {
        try {
            try {
                ServerBootstrap.init();
            } catch (PhysicalException e) {
                ServerMain.logger.error(e.getMessage());
                System.exit(0);
            }
            int port = ConfigLoader.resolveServerPort(args);
            ServerPipeline pipeline = new ServerPipeline(
                    new UdpRequestReader(),
                    new UdpRequestProcessor(),
                    new UdpResponseSender()
            );
            logger.info("Сервер запущен: коллекция загружена.");
            UdpConnectionModule connectionModule = new UdpConnectionModule(port);
            logger.info("UDP запущен на порту {}", port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                pipeline.stop();
                Loader.save();
            }));
            RuntimeConfig appliedConfig = ConfigLoader.getRuntimeConfig();
            logger.info("Начальная runtime-конфигурация: port={} ({}), packet_capacity={} ({}), socket_timeout_ms={} ({}), socket_receive_buffer={} ({})",
                    appliedConfig.serverPort(),
                    appliedConfig.serverPortSource(),
                    appliedConfig.packetCapacityBytes(),
                    appliedConfig.packetCapacitySource(),
                    appliedConfig.socketTimeoutMs(),
                    appliedConfig.socketTimeoutSource(),
                    appliedConfig.socketReceiveBufferBytes(),
                    appliedConfig.socketReceiveBufferSource());

            pipeline.start(connectionModule.socket());

            while (true) {
                RuntimeConfig runtimeConfig = ConfigLoader.reloadRuntimeConfig();
                logConfigChanges(appliedConfig, runtimeConfig);
                connectionModule.applyRuntimeSettings(runtimeConfig);
                UdpResponseSender.applyRuntimeSettings(runtimeConfig);
                if (runtimeConfig.serverPort() != connectionModule.port()) {
                    connectionModule.rebind(runtimeConfig.serverPort());
                    // Note: if port changes, we might need to restart pipeline with new socket
                    // For now, assume port stays same or pipeline handles it.
                }
                appliedConfig = runtimeConfig;
                Thread.sleep(1000); // Prevent busy loop
            }
        } catch (IOException e) {
            logger.error("Сервер завершился с ошибкой ввода-вывода", e);
        } catch (Exception e) {
            logger.error("Ошибка server pipeline", e);
        }
    }

    private static void logConfigChanges(RuntimeConfig previous, RuntimeConfig current) {
        if (previous.serverPort() != current.serverPort()) {
            logger.info("Изменение config: MUSIC_BANDS_SERVER_PORT {} ({}) -> {} ({})",
                    previous.serverPort(), previous.serverPortSource(), current.serverPort(), current.serverPortSource());
        }
        if (previous.packetCapacityBytes() != current.packetCapacityBytes()) {
            logger.info("Изменение config: MUSIC_BANDS_PACKET_CAPACITY {} ({}) -> {} ({})",
                    previous.packetCapacityBytes(), previous.packetCapacitySource(),
                    current.packetCapacityBytes(), current.packetCapacitySource());
        }
        if (previous.socketTimeoutMs() != current.socketTimeoutMs()) {
            logger.info("Изменение config: MUSIC_BANDS_SOCKET_TIMEOUT_MS {} ({}) -> {} ({})",
                    previous.socketTimeoutMs(), previous.socketTimeoutSource(),
                    current.socketTimeoutMs(), current.socketTimeoutSource());
        }
        if (previous.serverPort() == current.serverPort()
                && !previous.serverPortSource().equals(current.serverPortSource())) {
            logger.info("Источник config: MUSIC_BANDS_SERVER_PORT {} -> {}", previous.serverPortSource(), current.serverPortSource());
        }
        if (previous.packetCapacityBytes() == current.packetCapacityBytes()
                && !previous.packetCapacitySource().equals(current.packetCapacitySource())) {
            logger.info("Источник config: MUSIC_BANDS_PACKET_CAPACITY {} -> {}",
                    previous.packetCapacitySource(), current.packetCapacitySource());
        }
        if (previous.socketTimeoutMs() == current.socketTimeoutMs()
                && !previous.socketTimeoutSource().equals(current.socketTimeoutSource())) {
            logger.info("Источник config: MUSIC_BANDS_SOCKET_TIMEOUT_MS {} -> {}",
                    previous.socketTimeoutSource(), current.socketTimeoutSource());
        }
        if (previous.socketReceiveBufferBytes() == current.socketReceiveBufferBytes()
                && !previous.socketReceiveBufferSource().equals(current.socketReceiveBufferSource())) {
            logger.info("Источник config: MUSIC_BANDS_SOCKET_RECEIVE_BUFFER_BYTES {} -> {}",
                    previous.socketReceiveBufferSource(), current.socketReceiveBufferSource());
        }
    }
}
