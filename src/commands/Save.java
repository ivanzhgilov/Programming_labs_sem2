package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import utility.HistoryParser;
import utility.loader.Loader;

public class Save extends Command{
    public Save(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "сохранить коллекцию в файл";
    }

    @Override
    public String toString() {
        return "save";
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
        Loader.save();
    }

}
