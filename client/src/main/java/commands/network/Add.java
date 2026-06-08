package commands.network;

import commands.CommandResult;
import creauters.MusicBandCreature;
import structs.MusicBand;

import java.util.List;

public class Add extends Base {
    @Override
    public CommandResult execute(String[] tokens) {
        List<String> args = parseArgs(tokens);
        if (!args.isEmpty()) {
            System.out.printf("У %s нет параметров\n", "add");
            return CommandResult.continueWithoutRequest();
        }
        MusicBand bandPayload = MusicBandCreature.creatureMusicBand(MusicBand.builder());
        return request("add", args, bandPayload, null);
    }
}
