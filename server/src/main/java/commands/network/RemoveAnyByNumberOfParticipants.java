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

public class RemoveAnyByNumberOfParticipants extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "remove_any_by_number_of_participants");

            int value = ArgumentValidator.parseIntArgument(args.get(0), "numberOfParticipants", "remove_any_by_number_of_participants");

            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "remove_any_by_number_of_participants");

            int removedId = CollectionManager.removeAnyByNumberOfParticipantsWithDb(value, ownerId);

            if (removedId < 0) {
                return ok("В вашей коллекции не найдено элементов с количеством участников: " + value);
            }

            return ok("Из базы данных и памяти успешно удалён ваш элемент с id: " + removedId);

        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при дешифровании пакета данных: " + e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении remove_any_by_number_of_participants: " + e.getMessage());
        }
    }
}