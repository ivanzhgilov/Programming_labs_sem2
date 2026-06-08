package managers;

import exceptions.CommandNotFoundException;
import main.ClientScriptExecutor;
import commands.Command;
import commands.CommandResult;
import commands.local.ExecuteScript;
import commands.local.Exit;
import commands.network.*;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();


    public CommandManager(ClientScriptExecutor scriptExecutor) {
        commands.put("help", new CommandWithoutArgs("help"));
        commands.put("info", new CommandWithoutArgs("info"));
        commands.put("show", new CommandWithoutArgs("show"));
        commands.put("print_descending", new CommandWithoutArgs("print_descending"));
        commands.put("history", new CommandWithoutArgs("history"));
        commands.put("clear", new CommandWithoutArgs("clear"));
        commands.put("remove_any_by_number_of_participants", new CommandWithoutArgs("remove_any_by_number_of_participants"));
        commands.put("remove_by_id", new RemoveById());
        commands.put("exit", new Exit());
        commands.put("execute_script", new ExecuteScript(scriptExecutor));
        commands.put("add", new Add());
        commands.put("add_if_min", new AddIfMin());
        commands.put("update", new Update());
        commands.put("count_greater_than_studio", new CountGreaterThanStudio());
        commands.put("remove_lower", new RemoveLower());
        commands.put("login", new Login());
        commands.put("register", new Register());
        commands.put("logout", new commands.local.Logout());
    }


    public CommandResult processLine(String line) throws Exception {
        String[] tokens = line.trim().split(" ");
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            return CommandResult.continueWithoutRequest();
        }
        try {
            Command command;
            if (commands.containsKey(tokens[0])) {
                command = commands.get(tokens[0]);
            } else {
                throw new CommandNotFoundException(tokens[0]);
            }
            return command.execute(tokens);
        } catch (CommandNotFoundException e) {
            System.out.println(e.getMessage());
            return CommandResult.continueWithoutRequest();
        }
    }
}
