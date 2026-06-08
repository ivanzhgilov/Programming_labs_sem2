package commands.network;

import commands.base.BaseHandler;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;
import utility.UserContext;

public class PrintDescending extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "print_descending");
            return ok(CollectionManager.printDescendingText());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении print_descending: " + e.getMessage());
        }
    }
}