package commands;

import creauters.MusicBandCreature;
import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import structs.MusicBand;
import managers.CollectionManager;
import utility.HistoryParser;

public class AddIfMin extends Command {
    public AddIfMin(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции";
    }

    @Override
    public String toString() {
        return "add_if_min";
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
        System.out.println("Инициализировано создание объекта для попытки добавления его в коллекцию");
        MusicBand result = MusicBandCreature.creatureMusicBand(MusicBand.builder());
        System.out.println("Создание MusicBand завершено!");
        boolean flag = CollectionManager.addIfMin(result);
        if (flag) {
            System.out.println("Созданный MusicBand успешно добавлен! Его id: " + result.getId());
        } else {
            System.out.println("Созданный MusicBand не был добавлен!");
        }

    }
}
