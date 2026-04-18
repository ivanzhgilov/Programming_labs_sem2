package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import managers.CollectionManager;
import utility.HistoryParser;

public class Show extends Command {
    public Show(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }

    @Override
    public String toString() {
        return "show";
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

        System.out.println("Информация об элементах коллекции");
        CollectionManager.showAll();
    }
}
