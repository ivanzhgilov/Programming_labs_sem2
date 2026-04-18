package commands;

import creauters.MusicBandCreature;
import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import structs.MusicBand;
import managers.CollectionManager;
import utility.HistoryParser;

public class Add extends Command {
    public Add(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return this + "добавить новый элемент в коллекцию";
    }

    @Override
    public String toString() {
        return "add";
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
        System.out.println("Инициализировано создание объекта для добавления его в коллекцию");
        MusicBand result = MusicBandCreature.creatureMusicBand(MusicBand.builder());
        CollectionManager.add(result);
        System.out.println("Создание MusicBand завершено! Его id: " + result.getId());
    }
}
