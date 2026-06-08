package commands.local;

import commands.Command;
import commands.CommandResult;

public class Exit implements Command {
    @Override
    public CommandResult execute(String[] tokens) {
        System.out.println("Работа клиента завершена.");
        return CommandResult.stopClient();
    }
}
