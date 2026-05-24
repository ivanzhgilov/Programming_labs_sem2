package commands.network;

import commands.CommandResult;
import creauters.MusicBandCreature;
import structs.MusicBand;

import java.util.List;

public class AddIfMin extends Base {
    @Override
    public CommandResult execute(String[] tokens) {
        List<String> args = parseArgs(tokens);
        if (!args.isEmpty()) {
            System.out.printf("У %s нет параметров\n", "add_if_min");
            return CommandResult.continueWithoutRequest();
        }
        // id is generated on the server; client sends id=0.
        MusicBand bandPayload = MusicBandCreature.creatureMusicBand(MusicBand.builder());
        return request("add_if_min", args, bandPayload, null);
    }
}
