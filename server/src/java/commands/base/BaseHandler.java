package commands.base;

import exceptions.ErrorLevel;
import protocol.CommandResponse;

public abstract class BaseHandler implements Handler {
    
    protected CommandResponse ok(String message) {
        return CommandResponse.success(message);
    }

    protected CommandResponse fail(String message) {
        return CommandResponse.error(message, ErrorLevel.SERVER);
    }

    protected CommandResponse presentationError(String message) {
        return CommandResponse.error(message, ErrorLevel.CLIENT);
    }

    protected CommandResponse physicalError(String message) {
        return CommandResponse.error(message, ErrorLevel.PHYSICAL);
    }
}
