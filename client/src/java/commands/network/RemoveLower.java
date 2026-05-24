package commands.network;

import commands.CommandResult;
import creauters.IdCreature;

import java.util.List;

public class RemoveLower extends Base {
    @Override
    public CommandResult execute(String[] tokens) {
        List<String> args = parseArgs(tokens);
        if (!args.isEmpty()) {
            System.out.printf("У %s нет параметров\n", "remove_lower");
            return CommandResult.continueWithoutRequest();
        }
        args = List.of(String.valueOf(IdCreature.createId()));
        return request("remove_lower", args, null, null);
    }
}
