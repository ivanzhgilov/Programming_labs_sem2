package exceptions;

public enum InitializationExceptionType {
    NULL;

    @Override
    public String toString() {
        return (switch (this) {
            case NULL -> "Ошибка инициализации, одно из обязательных полей null";
        });
    }
}
