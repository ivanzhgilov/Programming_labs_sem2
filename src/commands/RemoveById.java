package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidValueCommandArgument;
import managers.CollectionManager;
import utility.HistoryParser;

public class RemoveById extends Command{
    private int id;

    public RemoveById(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "удалить элемент из коллекции по его id";
    }

    @Override
    public String toString() {
        return "remove_by_id";
    }

    @Override
    public String paramsToString() {
        return "id";
    }
    @Override
    public void execute() {
        HistoryParser.addCommand(toString());
        boolean flag = CollectionManager.checkId(id);
        if (flag) {
            CollectionManager.removeByID(id);
            System.out.println("Элемент с id: " + id + " удален");
        }
        else {
            System.out.println("Элемента с id:" + id + " не существует");
        }

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
}
