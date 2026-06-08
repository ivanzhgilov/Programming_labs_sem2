package main;

import network.*;
import protocol.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
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
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            readerPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.debug("Поток чтения {} запущен", threadName);
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        UdpRequestEnvelope envelope = requestReader.read(socket);
                        if (envelope != null) {
                            requestQueue.put(envelope);
                            logger.debug("Поток {}: запрос {} прочитан и добавлен в очередь", threadName, envelope.messageId());
                        }
                    } catch (SocketTimeoutException ignored) {
                    } catch (IOException | ClassNotFoundException e) {
                        logger.warn("Ошибка при чтении пакета в потоке {}: {}", threadName, e.getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error("Критическая ошибка в потоке чтения {}", threadName, e);
                    }
                }
                logger.info("Поток чтения {} завершил работу", threadName);
            });
        }

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            processorPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.debug("Поток обработки {} запущен", threadName);
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        UdpRequestEnvelope envelope = requestQueue.take();
                        logger.debug("Поток {}: взял запрос {} в обработку", threadName, envelope.messageId());
                        CommandResponse response = requestProcessor.process(envelope);
                        responseQueue.put(new UdpResponseEnvelope(envelope.clientAddress(), response, envelope.messageId()));
                        logger.debug("Поток {}: запрос {} обработан и передан на отправку", threadName, envelope.messageId());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error("Критическая ошибка в потоке обработки {}", threadName, e);
                    }
                }
                logger.info("Поток обработки {} завершил работу", threadName);
            });
        }

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            senderPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.debug("Поток отправки {} запущен", threadName);
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        UdpResponseEnvelope respEnv = responseQueue.take();
                        logger.debug("Поток {}: отправляет ответ на запрос {}", threadName, respEnv.messageId());
                        responseSender.send(socket, respEnv.clientAddress(), respEnv.response(), respEnv.messageId());
                        logger.debug("Поток {}: ответ на запрос {} успешно отправлен", threadName, respEnv.messageId());
                    } catch (IOException e) {
                        logger.error("Ошибка при отправке ответа в потоке {}: {}", threadName, e.getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error("Критическая ошибка в потоке отправки {}", threadName, e);
                    }
                }
                logger.info("Поток отправки {} завершил работу", threadName);
            });
        }
    }

    public void stop() {
        readerPool.shutdownNow();
        processorPool.shutdownNow();
        senderPool.shutdownNow();
    }
}
