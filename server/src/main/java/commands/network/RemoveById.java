package commands.network;

import commands.base.BaseHandler;
import exceptions.PresentationException;
import managers.AuthService;
import managers.CollectionManager;
import managers.SecurityVerifier;
import utility.ArgumentValidator;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;
import utility.UserContext;

import java.util.List;

public class RemoveById extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "remove_by_id");

            int id = ArgumentValidator.parseIntArgument(args.get(0), "id", "remove_by_id");

            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "remove_by_id");

            boolean removed = CollectionManager.removeByIDWithDb(id, ownerId);

            if (removed) {
                return ok("Элемент с id " + id + " успешно удалён из базы данных и памяти.");
            } else {
                return physicalError("Элемент с id " + id + " не найден в вашей коллекции (возможно, он принадлежит другому пользователю).");
            }

        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при дешифровании пакета данных: " + e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении remove_by_id: " + e.getMessage());
        }
    }
}