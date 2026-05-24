package protocol;

import exceptions.ErrorLevel;

import java.io.Serial;
import java.io.Serializable;


public record CommandResponse(boolean success, String message, ErrorLevel errorLevel) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;


    public static CommandResponse success(String message) {
        return new CommandResponse(true, message, null);
    }


    public static CommandResponse error(String message, ErrorLevel errorLevel) {
        return new CommandResponse(false, message, errorLevel);
    }
}
