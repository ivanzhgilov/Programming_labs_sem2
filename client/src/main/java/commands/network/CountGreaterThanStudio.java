package commands.network;

import commands.CommandResult;
import creauters.StudioCreature;
import structs.Studio;

import java.util.List;

public class CountGreaterThanStudio extends Base {
    @Override
    public CommandResult execute(String[] tokens) {
        List<String> args = parseArgs(tokens);
        if (!args.isEmpty()) {
            System.out.printf("У %s нет параметров\n", "count_greater_than_studio");
            return CommandResult.continueWithoutRequest();
        }
        Studio studioPayload = StudioCreature.creatureStudio();
        return request("count_greater_than_studio", parseArgs(tokens), null, studioPayload);
    }
}
