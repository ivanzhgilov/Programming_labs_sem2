package exceptions;

import java.io.Serial;

/**
 * Исключение для неправильного типа данных.
 * Возникает когда переданный тип не соответствует ожидаемому.
 * 
 * Примеры:
 * - Аргумент должен быть числом, но это текст
 * - Поле должно быть строкой, но получено число
 */
public class InvalidTypeException extends PresentationException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTypeException(String message) {
        super(message);
    }

    public InvalidTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создает исключение с информацией о полях ожидается/получено
     */
    public static InvalidTypeException create(String fieldName, String expectedType, String actualType) {
        return new InvalidTypeException(
            String.format("Поле '%s': ожидается %s, получено %s", fieldName, expectedType, actualType)
        );
    }
}
