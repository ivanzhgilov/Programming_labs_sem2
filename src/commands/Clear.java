package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import managers.CollectionManager;

import utility.HistoryParser;

public class Clear extends Command {
    public Clear(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "очистить коллекцию";
    }

    @Override
    public String toString() {
        return "clear";
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
        HistoryParser.addCommand(toString());

        CollectionManager.clear();
        System.out.println("Коллекция очищена");
    }
}
