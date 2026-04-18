package commands;

import creauters.IdCreature;
import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import managers.CollectionManager;
import utility.HistoryParser;

public class RemoveLower extends Command {
    public RemoveLower(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "удалить из коллекции все элементы, меньшие, чем заданный";
    }

    @Override
    public String toString() {
        return "remove_lower";
    }

    @Override
    public void execute() {
        HistoryParser.addCommand(toString());
        System.out.println("Элементы с id меньше чем заданный будут удалены");
        int id = IdCreature.createId();
        long count = CollectionManager.removeLower(id);
        System.out.println("Удалено " + count + " элементов");
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
}
