package network;

import exceptions.CommandNotFoundException;
import exceptions.ErrorLevel;
import managers.RequestRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.CommandResponse;

public class UdpRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(UdpRequestProcessor.class);
    private final RequestRegistry requestRegistry = new RequestRegistry();

    public CommandResponse process(UdpRequestEnvelope envelope) {
        try {
            return requestRegistry.execute(envelope.request());
        } catch (CommandNotFoundException e) {
            logger.debug("Команда не найдена: {}", envelope.request().commandName());
            return CommandResponse.error(e.getMessage(), ErrorLevel.CLIENT);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при обработке команды {}: {}",
                    envelope.request().commandName(), e.getMessage(), e);
            return CommandResponse.error("На сервере произошла ошибка. Попробуйте позже.", ErrorLevel.PHYSICAL);
        }
    }
}
