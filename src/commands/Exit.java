package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import utility.HistoryParser;

public class Exit extends Command {
    public Exit(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "завершить программу (без сохранения в файл)";
    }

    @Override
    public String toString() {
        return "exit";
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
        System.out.println("Работа программы завершена");
        System.exit(0);
    }

}
