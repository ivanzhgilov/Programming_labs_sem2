package commands.network;

import commands.base.BaseHandler;
import exceptions.PresentationException;
import managers.CollectionManager;
import utility.ArgumentValidator;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;
import utility.UserContext;

import java.util.List;

public class CountGreaterThanStudio extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "count_greater_than_studio");

            String studioAddress = args.get(0);

            long count = CollectionManager.countGreaterThanStudio(studioAddress);

            int ownerId = UserContext.getUserId();
            HistoryParser.addCommandForUser(ownerId, "count_greater_than_studio");

            return CommandResponse.success("Count of bands with studio address greater than " + studioAddress, count);

        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении count_greater_than_studio: " + e.getMessage());
        }
    }
}