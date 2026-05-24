package main;

import network.*;
import protocol.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.*;

/**
 * Серверный pipeline: reader -> processor -> sender.
 * Использует очереди и пулы потоков для многопоточной обработки.
 */
public class ServerPipeline {
    private static final Logger logger = LoggerFactory.getLogger(ServerPipeline.class);

    private final UdpRequestReader requestReader;
    private final UdpRequestProcessor requestProcessor;
    private final UdpResponseSender responseSender;

    private final BlockingQueue<UdpRequestEnvelope> requestQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<UdpResponseEnvelope> responseQueue = new LinkedBlockingQueue<>();

    private final ExecutorService readerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ExecutorService processorPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ExecutorService senderPool = Executors.newCachedThreadPool();

    public ServerPipeline(UdpRequestReader requestReader, UdpRequestProcessor requestProcessor, UdpResponseSender responseSender) {
        this.requestReader = requestReader;
        this.requestProcessor = requestProcessor;
        this.responseSender = responseSender;
    }

    public void start(DatagramSocket socket) {
        // Запуск потоков чтения
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            readerPool.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        UdpRequestEnvelope envelope = requestReader.read(socket);
                        if (envelope != null) {
                            requestQueue.put(envelope);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        logger.warn("Ошибка при чтении пакета: {}", e.getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error("Критическая ошибка в потоке чтения", e);
                    }
                }
            });
        }

        // Запуск потоков обработки
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            processorPool.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        UdpRequestEnvelope envelope = requestQueue.take();
                        CommandResponse response = requestProcessor.process(envelope);
                        responseQueue.put(new UdpResponseEnvelope(envelope.clientAddress(), response, envelope.messageId()));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error("Критическая ошибка в потоке обработки", e);
                    }
                }
            });
        }

        // Запуск потоков отправки
        senderPool.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    UdpResponseEnvelope respEnv = responseQueue.take();
                    responseSender.send(socket, respEnv.clientAddress(), respEnv.response(), respEnv.messageId());
                } catch (IOException e) {
                    logger.error("Ошибка при отправке ответа", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("Критическая ошибка в потоке отправки", e);
                }
            }
        });
    }

    public void stop() {
        readerPool.shutdownNow();
        processorPool.shutdownNow();
        senderPool.shutdownNow();
    }
}
