package commands.network;

import commands.base.BaseHandler;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;

public class Clear extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            CollectionManager.clear();
            HistoryParser.addCommand("clear");
            return ok("Коллекция очищена");
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
