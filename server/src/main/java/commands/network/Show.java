package commands.network;

import commands.base.BaseHandler;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;
import utility.UserContext;

public class Show extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "show");
            return ok(CollectionManager.showAllElements());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении show: " + e.getMessage());
        }
    }
}