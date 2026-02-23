package commands;

import exceptions.InvalidInputException;


/**
 * Выводит список доступных команд
 */

public class Help extends Command {
    public Help(Object parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        if (this.parameter != null) {
            throw new InvalidInputException("У команды help не должно быть аргументов");
        }
        return true;
    }
}
