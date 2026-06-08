package commands.network;

import commands.Command;
import commands.CommandResult;
import protocol.CommandRequest;
import structs.MusicBand;
import structs.Studio;

import java.util.Arrays;
import java.util.List;
import managers.SessionManager;
import utility.CryptoUtils;


public abstract class Base implements Command {
    protected CommandResult request(String commandName, List<String> args, MusicBand bandPayload, Studio studioPayload) {
        CommandRequest request = new CommandRequest(
                commandName,
                args,
                bandPayload,
                studioPayload,
                CryptoUtils.encrypt(SessionManager.getCurrentLogin()),
                CryptoUtils.encrypt(SessionManager.getCurrentPassword())
        );
        return CommandResult.continueWithRequest(request);
    }


    protected List<String> parseArgs(String[] tokens) {
        if (tokens.length <= 1) {
            return List.of();
        }
        return Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
    }
}
