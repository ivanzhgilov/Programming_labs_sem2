package commands;


import creauters.StudioCreature;
import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import structs.Studio;
import managers.CollectionManager;
import utility.HistoryParser;

public class CountGreaterThanStudio extends Command {
    public CountGreaterThanStudio(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "вывести количество элементов, значение поля studio которых больше заданного";
    }
    @Override
    public String toString() {
        return "count_greater_than_studio";
    }

    @Override
    public void execute() {
        Studio studio = StudioCreature.creatureStudio();
        HistoryParser.addCommand(toString());
        long result = CollectionManager.countGreaterThanStudio(studio);
        if (result == 0) {
            System.out.println("Нет элементов коллекции, у которых значение поля studio больше указанного");
        }
        else {
            System.out.println("Найдено " + result + "элементов");
        }
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
