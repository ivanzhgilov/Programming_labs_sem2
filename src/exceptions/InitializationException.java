package exceptions;

public class InitializationException extends Exception {
    public InitializationException(InitializationExceptionType type) {
        super(type.toString());
    }
}
