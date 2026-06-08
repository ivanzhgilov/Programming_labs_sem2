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

public class RemoveLower extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "remove_lower");

            int id = ArgumentValidator.parseIntArgument(args.get(0), "id", "remove_lower");
            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "remove_lower");

            long count = CollectionManager.removeLowerWithDb(id, ownerId);

            return ok("Удаление завершено. Из СУБД и памяти удалено " + count + " ваших элементов, которые меньше указанного порога.");

        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при дешифровании пакета: " + e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении remove_lower: " + e.getMessage());
        }
    }
}