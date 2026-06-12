package commands.network;

import commands.base.BaseHandler;
import exceptions.InvalidFieldValueException;
import managers.CollectionManager;
import utility.HistoryParser;
import utility.UserContext;
import protocol.CommandRequest;
import protocol.CommandResponse;
import structs.MusicBand;

public class Add extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            MusicBand incoming = request.bandPayload();
            if (incoming == null) {
                return presentationError("Для команды add требуется объект MusicBand.");
            }

            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "show");

            int generatedId = CollectionManager.addWithDb(incoming, ownerId);

            if (generatedId > 0) {
                MusicBand createdBand = CollectionManager.getSet().stream()
                        .filter(b -> b.getId() == generatedId)
                        .findFirst()
                        .orElse(null);
                return CommandResponse.success("Создание MusicBand успешно завершено! Объект сохранен в БД под id: " + generatedId, createdBand);
            } else {
                return physicalError("Не удалось сохранить объект в базу данных.");
            }

        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении add: " + e.getMessage());
        }
    }
}