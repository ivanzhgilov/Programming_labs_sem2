package exceptions;

public enum ArgumentExceptionType {
    NOT_NULL_ARGUMENT,
    INCORRECT_ARGUMENTS;

    @Override
    public String toString() {
        return (switch (this){
            case NOT_NULL_ARGUMENT -> "нет аргументов";
            case INCORRECT_ARGUMENTS -> "должны быть аргументы";
        });
    }
}
