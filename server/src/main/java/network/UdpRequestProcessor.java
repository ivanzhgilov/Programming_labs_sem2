package network;

import exceptions.CommandNotFoundException;
import exceptions.ErrorLevel;
import managers.AuthService;
import managers.RequestRegistry;
import managers.SecurityVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.UserContext;

public class UdpRequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(UdpRequestProcessor.class);
    private final RequestRegistry requestRegistry = new RequestRegistry();
    private final AuthService authService = AuthService.getInstance();

    public CommandResponse process(UdpRequestEnvelope envelope) {
        CommandRequest request = envelope.request();
        String commandName = request.commandName();

        try {
            String decryptedLogin = SecurityVerifier.verifyAndDecrypt(request.login());
            String decryptedPassword = SecurityVerifier.verifyAndDecrypt(request.password());

            if (!commandName.equals("login") && !commandName.equals("register")) {

                Integer ownerId = authService.getValidatedUserId(decryptedLogin, decryptedPassword);
                if (ownerId == null) {
                    return CommandResponse.error("Вы не авторизованы или сессия истекла.", ErrorLevel.CLIENT);
                }
                UserContext.setUserId(ownerId);
            }

            return requestRegistry.execute(request);

        } catch (SecurityException e) {
            logger.warn(e.getMessage());
            return CommandResponse.error("Ошибка безопасности: запрос отклонен.", ErrorLevel.CLIENT);
        } catch (Exception e) {
            logger.error("Ошибка при обработке команды {}: {}", commandName, e.getMessage(), e);
            return CommandResponse.error("На сервере произошла ошибка.", ErrorLevel.PHYSICAL);
        } finally {
            UserContext.clear();
        }
    }
}
