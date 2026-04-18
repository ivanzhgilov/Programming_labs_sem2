package commands;

import exceptions.InvalidCommandArgumentException;
import exceptions.ArgumentExceptionType;
import managers.CollectionManager;
import utility.HistoryParser;

public class Info extends Command {
    public Info(String... parameters) {
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
    public String description() {
        return this + "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }

    @Override
    public String toString(){
        return "info";
    }

    @Override
    public void execute() {
        HistoryParser.addCommand(toString());
        System.out.println("Информация о коллекции");
        CollectionManager.info();
    }
}
