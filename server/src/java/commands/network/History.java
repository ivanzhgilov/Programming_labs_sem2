package commands.network;

import commands.base.BaseHandler;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;

import java.util.List;
import java.util.stream.Collectors;

public class History extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            HistoryParser.addCommand("history");
            List<String> commandsList = HistoryParser.getCommandsList();
            int endIndex = Math.max(0, commandsList.size() - 8);
            return ok(String.join("\n", commandsList.subList(endIndex, commandsList.size())) + "\n");
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
