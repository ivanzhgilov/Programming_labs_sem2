package commands.network;

import commands.base.BaseHandler;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;

public class PrintDescending extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            HistoryParser.addCommand("print_descending");
            return ok(CollectionManager.printDescendingText());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
