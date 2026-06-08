package commands.network;

import commands.base.BaseHandler;
import managers.AuthService;
import managers.SecurityVerifier;
import protocol.CommandRequest;
import protocol.CommandResponse;

public class Register extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            String clearLogin = SecurityVerifier.verifyAndDecrypt(request.login());
            String clearPassword = SecurityVerifier.verifyAndDecrypt(request.password());

            if (clearLogin.trim().isEmpty() || clearPassword.trim().isEmpty()) {
                return presentationError("Логин и пароль не могут быть пустыми.");
            }

            boolean success = AuthService.getInstance().register(clearLogin, clearPassword);

            if (success) {
                return ok("Регистрация прошла успешно!");
            } else {
                return presentationError("Пользователь с логином '" + clearLogin + "' уже существует.");
            }

        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при дешифровании регистрационных данных.");
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при регистрации: " + e.getMessage());
        }
    }
}