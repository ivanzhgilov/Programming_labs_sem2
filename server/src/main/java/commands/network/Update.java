package commands.network;

import commands.base.BaseHandler;
import exceptions.PresentationException;
import managers.AuthService;
import managers.CollectionManager;
import managers.SecurityVerifier;
import utility.ArgumentValidator;
import protocol.CommandRequest;
import protocol.CommandResponse;
import structs.MusicBand;
import utility.HistoryParser;
import utility.UserContext;

import java.util.List;

public class Update extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "update");

            int id = ArgumentValidator.parseIntArgument(args.get(0), "id", "update");

            MusicBand incoming = request.bandPayload();
            if (incoming == null) {
                return presentationError("Для команды update требуется объект MusicBand.");
            }

            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "update");

            boolean success = CollectionManager.updateWithDb(id, incoming, ownerId);

            if (success) {
                return ok("Объект с id: " + id + " успешно изменен в БД и памяти.");
            } else {
                return presentationError("Ошибка: Объект с ID " + id + " не найден в вашей коллекции или вы не являетесь его владельцем.");
            }

        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при обработке транзакции: " + e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении update: " + e.getMessage());
        }
    }
}