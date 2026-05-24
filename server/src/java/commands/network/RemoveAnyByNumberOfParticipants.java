package commands.network;

import commands.base.BaseHandler;
import exceptions.PresentationException;
import managers.CollectionManager;
import utils.ArgumentValidator;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;

import java.util.List;

public class RemoveAnyByNumberOfParticipants extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "remove_any_by_number_of_participants");
            
            int value = ArgumentValidator.parseIntArgument(args.get(0), "numberOfParticipants", "remove_any_by_number_of_participants");
            
            int removedId = CollectionManager.removeAnyByNumberOfParticipants(value);
            HistoryParser.addCommand("remove_any_by_number_of_participants");
            if (removedId < 0) {
                return ok("Подходящих элементов не найдено");
            }
            return ok("Удалён элемент с id: " + removedId);
        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
