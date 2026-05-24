package commands.network;

import commands.base.BaseHandler;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;

public class Info extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            HistoryParser.addCommand("info");
            return ok(CollectionManager.info());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
