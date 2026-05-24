package exceptions;

import java.io.Serializable;

/**
 * Уровни ошибок в приложении:
 * PRESENTATION - ошибки на уровне представления (валидация клиента)
 * BUSINESS_LOGIC - ошибки бизнес-логики (сервер)
 * PHYSICAL - ошибки физического уровня (персистентность, файлы)
 */
public enum ErrorLevel implements Serializable {
    CLIENT("Ошибка клиента"),
    SERVER("Ошибка сервера"),
    PHYSICAL("Проблема с файлом");

    private final String description;

    ErrorLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
