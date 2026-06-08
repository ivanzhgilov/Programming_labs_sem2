package exceptions;

import java.io.Serial;

/**
 * Исключения физического уровня (персистентность, файловая система).
 * Возникают при ошибках:
 * - чтения/записи файлов
 * - сериализации/десериализации данных
 * - доступа к хранилищу
 */
public class PhysicalException extends AppException {
    @Serial
    private static final long serialVersionUID = 1L;

    public PhysicalException(String message) {
        super(message);
    }

    public PhysicalException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public ErrorLevel getErrorLevel() {
        return ErrorLevel.PHYSICAL;
    }
}
