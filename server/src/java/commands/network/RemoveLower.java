package commands.network;

import commands.base.BaseHandler;
import exceptions.PresentationException;
import managers.CollectionManager;
import utils.ArgumentValidator;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;

import java.util.List;

public class RemoveLower extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "remove_lower");
            
            int id = ArgumentValidator.parseIntArgument(args.get(0), "id", "remove_lower");
            
            long count = CollectionManager.removeLower(id);
            HistoryParser.addCommand("remove_lower");
            return ok("Удалено " + count + " элементов");
        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
