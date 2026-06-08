package exceptions;

import java.io.Serial;

/**
 * Исключения уровня представления (валидация входных данных, ошибки пользователя).
 * Возникают при неправильном вводе данных на клиенте или некорректных аргументах команды.
 */
public class PresentationException extends AppException {
    @Serial
    private static final long serialVersionUID = 1L;

    public PresentationException(String message) {
        super(message);
    }

    public PresentationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public ErrorLevel getErrorLevel() {
        return ErrorLevel.CLIENT;
    }
}
