package commands.network;

import commands.base.BaseHandler;
import exceptions.PresentationException;
import managers.CollectionManager;
import utility.HistoryParser;
import utility.UserContext;
import protocol.CommandRequest;
import protocol.CommandResponse;
import structs.MusicBand;

public class AddIfMin extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            MusicBand incoming = request.bandPayload();
            if (incoming == null) {
                return presentationError("Для команды add_if_min требуется объект MusicBand.");
            }
            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "show");

            boolean added = CollectionManager.addIfMinWithDb(incoming, ownerId);

            if (added) {
                return ok("Элемент успешно добавлен, так как он является минимальным!");
            } else {
                return ok("Элемент не добавлен: в коллекции уже есть элементы, которые меньше или равны ему.");
            }

        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении add_if_min: " + e.getMessage());
        }
    }
}