package commands.network;

import commands.CommandResult;

import java.util.List;

public class CommandWithoutArgs extends Base {
    private final String commandName;

    public CommandWithoutArgs(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public CommandResult execute(String[] tokens) {
        List<String> args = parseArgs(tokens);
        if (!args.isEmpty()) {
            System.out.printf("У %s нет параметров\n", commandName);
            return CommandResult.continueWithoutRequest();
        }
        return request(commandName, parseArgs(tokens), null, null);
    }
}

