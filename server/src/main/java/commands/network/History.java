package commands.network;

import commands.base.BaseHandler;
import managers.AuthService;
import managers.SecurityVerifier;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;
import utility.UserContext;

import java.util.List;

public class History extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            int ownerId = UserContext.getUserId();

            HistoryParser.addCommandForUser(ownerId, "history");

            List<String> lastCommands = HistoryParser.getCommandsListForUser(ownerId);

            if (lastCommands.isEmpty()) {
                return ok("Ваша история команд пока пуста.");
            }

            return ok(String.join("\n", lastCommands) + "\n");

        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при дешифровании пакета данных: " + e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при получении истории: " + e.getMessage());
        }
    }
}