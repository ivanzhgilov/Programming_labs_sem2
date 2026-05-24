package exceptions;

import java.io.Serial;

/**
 * Исключение для недопустимого значения поля.
 * Возникает когда значение выходит за допустимые границы или нарушает ограничения.
 * 
 * Примеры:
 * - Координата X должна быть ≤ 617, но получено 618
 * - Количество участников должно быть > 0, но получено -5
 * - Строка не может быть пустой
 */
public class InvalidFieldValueException extends PresentationException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidFieldValueException(String message) {
        super(message);
    }

    public InvalidFieldValueException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создает исключение для граничного значения
     */
    public static InvalidFieldValueException outOfRange(String fieldName, String constraint) {
        return new InvalidFieldValueException(
            String.format("Поле '%s' вышло за границы: %s", fieldName, constraint)
        );
    }

    /**
     * Создает исключение для null значения
     */
    public static InvalidFieldValueException nullValue(String fieldName) {
        return new InvalidFieldValueException(
            String.format("Поле '%s' не может быть пустым", fieldName)
        );
    }

    /**
     * Создает исключение для пустой строки
     */
    public static InvalidFieldValueException emptyString(String fieldName) {
        return new InvalidFieldValueException(
            String.format("Поле '%s' не может быть пустой строкой", fieldName)
        );
    }
}
