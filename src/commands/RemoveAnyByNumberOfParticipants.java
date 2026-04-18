package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import managers.CollectionManager;
import utility.HistoryParser;

public class RemoveAnyByNumberOfParticipants extends Command {
    private Integer numberOfParticipants;

    public RemoveAnyByNumberOfParticipants(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "удалить из коллекции один элемент, значение поля numberOfParticipants которого эквивалентно заданному";
    }

    @Override
    public String toString() {
        return "remove_any_by_number_of_participants";
    }

    @Override
    public String paramsToString() {
        return "numberOfParticipants";
    }

    @Override
    public void execute() {
        HistoryParser.addCommand(toString());
        int result = CollectionManager.removeAnyByNumberOfParticipants(numberOfParticipants);
        if (result == -1) {
            System.out.println("Нет элемента коллекции с полем numberOfParticipants равным: " + numberOfParticipants);
        } else {
            System.out.println("Удален элемент коллекции с id: " + result);
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
            numberOfParticipants = Integer.parseInt(parameters[0]);
            if (numberOfParticipants < 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException e) {
            System.out.println("numberOfParticipants должно быть целым положительным числом меньшим " + Integer.MAX_VALUE);
            return false;
        }
    }

}
