package commands.network;

import commands.Command;
import commands.CommandResult;
import protocol.CommandRequest;
import structs.MusicBand;
import structs.Studio;

import java.util.Arrays;
import java.util.List;

public abstract class Base implements Command {
    protected CommandResult request(String commandName, List<String> args, MusicBand bandPayload, Studio studioPayload) {
        CommandRequest request = new CommandRequest(commandName, args, bandPayload, studioPayload);
        return CommandResult.continueWithRequest(request);
    }

    protected List<String> parseArgs(String[] tokens) {
        if (tokens.length <= 1) {
            return List.of();
        }
        return Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
    }
}
