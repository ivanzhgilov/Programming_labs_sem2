package commands.network;

import commands.Command;
import commands.CommandResult;
import creauters.AuthInputCreature;
import protocol.CommandRequest;
import utility.CryptoUtils;

import java.util.List;

public class Register implements Command {
    @Override
    public CommandResult execute(String[] tokens) throws Exception {
        String login = AuthInputCreature.readLogin();
        String password = "";

        while (true) {
            password = AuthInputCreature.readPassword("Введите пароль для регистрации: ");
            System.out.print("Повторите пароль: ");
            String confirmPassword = managers.InputManager.readPassword();

            if (password.equals(confirmPassword)) {
                break;
            } else {
                System.out.println("Пароли не совпадают. Пожалуйста, попробуйте снова.");
            }
        }

        CommandRequest request = new CommandRequest(
                "register",
                List.of(),
                null,
                null,
                CryptoUtils.encrypt(login),
                CryptoUtils.encrypt(password)
        );
        return CommandResult.continueWithRequest(request);
    }
}
