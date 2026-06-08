package commands.local;

import main.ClientScriptExecutor;
import commands.Command;
import commands.CommandResult;

public class ExecuteScript implements Command {
    private final ClientScriptExecutor scriptExecutor;

    public ExecuteScript(ClientScriptExecutor scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public CommandResult execute(String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Использование: execute_script <file_name>");
            return CommandResult.continueWithoutRequest();
        }
        return scriptExecutor.execute(tokens[1]) ? CommandResult.continueWithoutRequest() : CommandResult.stopClient();
    }
}
