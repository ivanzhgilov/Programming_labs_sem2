package commands.network;

import commands.base.BaseHandler;
import exceptions.PresentationException;
import managers.CollectionManager;
import utils.ArgumentValidator;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utils.StructureValidator;
import structs.MusicBand;
import utility.HistoryParser;

import java.util.List;

public class Update extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            List<String> args = request.arguments();
            ArgumentValidator.validateArgumentCount(args, 1, "update");

            int id = ArgumentValidator.parseIntArgument(args.get(0), "id", "update");

            if (!CollectionManager.checkId(id)) {
                return fail("Объект с id: " + id + " не найден");
            }

            MusicBand incoming = request.bandPayload();
            if (incoming == null) {
                return presentationError("Для команды update требуется объект MusicBand.");
            }

            StructureValidator.validateMusicBand(incoming);

            MusicBand updated = CollectionManager.copyWithGeneratedId(incoming, id);
            CollectionManager.update(updated, id);
            HistoryParser.addCommand("update");
            return ok("Объект с id: " + id + " изменен");
        } catch (PresentationException e) {
            return presentationError(e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
