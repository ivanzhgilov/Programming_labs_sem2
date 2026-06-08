package exceptions;

import java.io.Serial;

/**
 * Базовый класс для всех исключений приложения.
 */
public abstract class AppException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Возвращает уровень ошибки для передачи клиенту.
     */
    public abstract ErrorLevel getErrorLevel();
}
