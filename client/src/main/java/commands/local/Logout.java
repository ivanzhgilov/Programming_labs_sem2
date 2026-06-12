package commands.local;

import commands.Command;
import commands.CommandResult;
import managers.SessionManager;

public class Logout implements Command {
    @Override
    public CommandResult execute(String[] tokens) throws Exception {
        SessionManager.clearSession();
        System.out.println("Вы вышли из системы.");
        return CommandResult.continueWithoutRequest();
    }
}
