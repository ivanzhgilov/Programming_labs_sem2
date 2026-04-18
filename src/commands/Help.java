package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;

import managers.CommandManager;
import utility.HistoryParser;


/**
 * Выводит список доступных команд
 */

public class Help extends Command {

    public Help(String... parameters) {
        super(parameters);
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameters.length > 0) {
                throw new InvalidCommandArgumentException(ArgumentExceptionType.NOT_NULL_ARGUMENT, this);
            }
            return true;
        } catch (InvalidCommandArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {
        HistoryParser.addCommand(this.toString());

        for(var el : CommandManager.commands.values()) {
            System.out.println(el.commandInfo());
        }
    }

    @Override
    public String description() {
        return "вывести справку по доступным командам";
    }

    @Override
    public String toString() {
        return "help";
    }
}
