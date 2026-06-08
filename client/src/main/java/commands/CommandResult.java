package commands;

import protocol.CommandRequest;

public record CommandResult(boolean continueClient, CommandRequest request) {
    public static CommandResult continueWithoutRequest() {
        return new CommandResult(true, null);
    }

    public static CommandResult continueWithRequest(CommandRequest request) {
        return new CommandResult(true, request);
    }

    public static CommandResult stopClient() {
        return new CommandResult(false, null);
    }
}
