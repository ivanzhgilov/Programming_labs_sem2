package exceptions;

import commands.Command;

public class InvalidCommandArgumentException extends Exception {
    private final Command command;
    private final ArgumentExceptionType type;
    public InvalidCommandArgumentException(ArgumentExceptionType type, Command command) {
        super();
        this.command = command;
        this.type = type;
    }

    @Override
    public String getMessage() {
        String space = " ";
        if (command.paramsToString().isEmpty()) {
            space = "";
        }
        return "у команды " + command + " " + type + space + command.paramsToString();
    }

}
