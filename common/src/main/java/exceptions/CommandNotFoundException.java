package exceptions;

import java.io.Serial;

/**
 * Исключение для команды, которая не найдена в реестре.
 */
public class CommandNotFoundException extends PresentationException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CommandNotFoundException(String commandName) {
        super(String.format("Команда %s не найдена. Используйте help чтобы посмотреть доступные команды", commandName));
    }

    public CommandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
