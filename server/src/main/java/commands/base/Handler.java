package commands.base;

import protocol.CommandRequest;
import protocol.CommandResponse;

public interface Handler {
    CommandResponse execute(CommandRequest request);
}
