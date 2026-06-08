package commands.network;

import commands.Command;
import commands.CommandResult;
import creauters.AuthInputCreature;
import protocol.CommandRequest;
import utility.CryptoUtils;

import java.util.List;

public class Login implements Command {
    @Override
    public CommandResult execute(String[] tokens) throws Exception {
        String login = AuthInputCreature.readLogin();
        String password = AuthInputCreature.readPassword("Введите пароль: ");

        CommandRequest request = new CommandRequest(
                "login",
                List.of(),
                null,
                null,
                CryptoUtils.encrypt(login),
                CryptoUtils.encrypt(password)
        );
        return CommandResult.continueWithRequest(request);
    }
}
