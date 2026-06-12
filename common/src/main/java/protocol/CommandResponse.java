package protocol;

import exceptions.ErrorLevel;

import java.io.Serial;
import java.io.Serializable;

public record CommandResponse(boolean success, String message, ErrorLevel errorLevel, Object payload) implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;

    public static CommandResponse success(String message) {
        return new CommandResponse(true, message, null, null);
    }

    public static CommandResponse success(String message, Object payload) {
        return new CommandResponse(true, message, null, payload);
    }

    public static CommandResponse error(String message, ErrorLevel errorLevel) {
        return new CommandResponse(false, message, errorLevel, null);
    }
}
