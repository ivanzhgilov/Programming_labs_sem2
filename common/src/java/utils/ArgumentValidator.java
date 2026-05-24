package utils;

import exceptions.InvalidFieldValueException;
import exceptions.InvalidTypeException;
import exceptions.PresentationException;

/**
 * Утилита для валидации аргументов команд.
 */
public class ArgumentValidator {
    
    /**
     * Проверяет что аргументов ровно нужное количество.
     */
    public static void validateArgumentCount(java.util.List<String> args, int expectedCount, 
                                             String commandName) throws PresentationException {
        if (args == null || args.size() != expectedCount) {
            throw new PresentationException(
                String.format("Команда '%s' требует %d аргумент(ов), получено %d",
                    commandName, expectedCount, args == null ? 0 : args.size())
            );
        }
    }


    /**
     * Парсит аргумент как целое число.
     */
    public static int parseIntArgument(String arg, String argumentName, 
                                       String commandName) throws InvalidTypeException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw InvalidTypeException.create(argumentName, "целое число", arg);
        }
    }
}
