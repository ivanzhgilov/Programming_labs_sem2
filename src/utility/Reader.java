package utility;


import managers.CommandManager;
import managers.InputManager;

import java.util.Arrays;

/**
 * Обработчик команд
 */

public class Reader {

    /**
     *
     * @param read обрабатываемая команда
     */
    public static void getLine(String read) {
        String[] line = read.split(" ");
        if (line[0].isEmpty() && !(line.length > 1)) {
            InputManager.startInput();
        } else {
            String command = line[0];
            String[] args = new String[line.length - 1];
            if (line.length > 1) {
                args = Arrays.copyOfRange(line, 1, line.length);
            }
            Reader.toCommand(command, args);
        }

    }

    private static void toCommand(String command, String... parameters) {
        CommandManager executableCommand = new CommandManager(parameters);
        executableCommand.runCommand(command);
    }
}
