package commands.network;

import commands.base.BaseHandler;
import exceptions.InvalidFieldValueException;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utils.StructureValidator;
import structs.MusicBand;
import utility.HistoryParser;

public class Add extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            MusicBand incoming = request.bandPayload();
            if (incoming == null) {
                return presentationError("Для команды add требуется объект MusicBand.");
            }

            StructureValidator.validateMusicBand(incoming);
            
            MusicBand stored = CollectionManager.copyWithGeneratedId(incoming, CollectionManager.getNextId());
            CollectionManager.add(stored);
            HistoryParser.addCommand("add");
            return ok("Создание MusicBand завершено! Его id: " + stored.getId());
        } catch (InvalidFieldValueException e) {
            return presentationError(e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
