package commands.network;

import commands.CommandResult;
import creauters.MusicBandCreature;
import structs.MusicBand;

import java.util.List;

public class Update extends Base {
    @Override
    public CommandResult execute(String[] tokens) {
        List<String> args = parseArgs(tokens);
        if (args.size() != 1) {
            System.out.println("Использование: update <id>");
            return CommandResult.continueWithoutRequest();
        }
        int id;
        try {
            id = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            System.out.println("id должен быть целым числом.");
            return CommandResult.continueWithoutRequest();
        }
        MusicBand bandPayload = MusicBandCreature.creatureMusicBand(MusicBand.builder().id(id));
        return request("update", args, bandPayload, null);
    }
}
