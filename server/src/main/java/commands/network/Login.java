package commands.network;

import commands.base.BaseHandler;
import managers.AuthService;
import managers.SecurityVerifier;
import protocol.CommandRequest;
import protocol.CommandResponse;

public class Login extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            String clearLogin = SecurityVerifier.verifyAndDecrypt(request.login());
            String clearPassword = SecurityVerifier.verifyAndDecrypt(request.password());

            boolean isValid = AuthService.getInstance().authenticate(clearLogin, clearPassword);

            if (isValid) {
                return ok("Авторизация успешна! Доступ к управлению коллекцией открыт.");
            } else {
                return presentationError("Неверный логин или пароль. Повторите попытку.");
            }

        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при дешифровании пакета аутентификации.");
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при авторизации: " + e.getMessage());
        }
    }
}