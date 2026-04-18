package commands;

import creauters.MusicBandCreature;
import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidValueCommandArgument;
import structs.MusicBand;
import managers.CollectionManager;
import utility.HistoryParser;

public class Update extends Command {
    private int id;

    public Update(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }

    @Override
    public String toString() {
        return "update";
    }

    @Override
    public String paramsToString() {
        return "id";
    }

    @Override
    public boolean validate() {
        try {

            if (this.parameters.length != 1) {
                throw new InvalidCommandArgumentException(ArgumentExceptionType.INCORRECT_ARGUMENTS, this);
            }
        } catch (InvalidCommandArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
        try {
            id = Integer.parseInt(parameters[0]);
            if (id <= 0) {
                throw new InvalidValueCommandArgument("");
            }
            return true;
        } catch (NumberFormatException | InvalidValueCommandArgument e) {
            System.out.println("id должно быть целым положительным числом");
            return false;
        }
    }

    @Override
    public void execute() {
        if (CollectionManager.checkId(this.id)) {
            HistoryParser.addCommand(toString());
            MusicBand band = MusicBandCreature.creatureMusicBand(MusicBand.builder().id(id));
            CollectionManager.update(band, id);
            System.out.println("Объект с id: " + this.id + " изменен");
        } else {
            System.out.println("Объект с id: " + this.id + " не найден");
        }
    }
}
