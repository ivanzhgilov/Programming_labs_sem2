package commands.network;

import commands.base.BaseHandler;
import managers.AuthService;
import managers.CollectionManager;
import managers.SecurityVerifier;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;
import utility.UserContext;

public class Clear extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "show");

            int removedCount = CollectionManager.clearWithDb(ownerId);

            if (removedCount > 0) {
                return ok("Очистка завершена! Из коллекции удалено ваших элементов: " + removedCount);
            } else {
                return ok("Очистка завершена. В коллекции не было элементов, принадлежащих вам.");
            }

        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при валидации сессии.");
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении clear: " + e.getMessage());
        }
    }
}