package managers;

import commands.*;

import java.util.HashMap;

/**
 * Класс, отвечающий за выполнение команд
 */
public class CommandManager {
    public static HashMap<String, Command> commands = new HashMap<>();

    public CommandManager(String... parameters) {
        commands.put("help", new Help(parameters));
        commands.put("add", new Add(parameters));
        commands.put("info", new Info(parameters));
        commands.put("show", new Show(parameters));
        commands.put("update", new Update(parameters));
        commands.put("remove_by_id", new RemoveById(parameters));
        commands.put("clear", new Clear(parameters));
        commands.put("save", new Save(parameters));
        commands.put("execute_script", new ExecuteScript(parameters));
        commands.put("exit", new Exit(parameters));
        commands.put("add_if_min", new AddIfMin(parameters));
        commands.put("remove_lower", new RemoveLower(parameters));
        commands.put("history", new History(parameters));
        commands.put("remove_any_by_number_of_participants", new RemoveAnyByNumberOfParticipants(parameters));
        commands.put("count_greater_than_studio", new CountGreaterThanStudio(parameters));
        commands.put("print_descending", new PrintDescending(parameters));
    }

    /**
     * Если команда найдена, то дальше проверяется на валидность и выполняется, если нет, то выполняется {@link Command}
     */
    public void runCommand(String commandName) {
        Command command;
        if (commands.containsKey(commandName)) {
            command = commands.get(commandName);
        } else {
            command = new Command();
        }
        if (command.validate()) {
            command.execute();
        } else {
            InputManager.startInput();
        }
    }
}
