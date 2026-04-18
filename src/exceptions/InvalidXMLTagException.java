package exceptions;

public class InvalidXMLTagException extends Exception {
    public InvalidXMLTagException(String message) {
        super("неожиданный тег: " + message);
    }
}
