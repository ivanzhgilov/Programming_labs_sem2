package commands.network;

import commands.base.BaseHandler;
import exceptions.InvalidFieldValueException;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utils.StructureValidator;
import structs.MusicBand;
import utility.HistoryParser;

public class AddIfMin extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            MusicBand incoming = request.bandPayload();
            if (incoming == null) {
                return presentationError("Для команды add_if_min требуется объект MusicBand.");
            }
            
            // Валидируем структуру данных
            StructureValidator.validateMusicBand(incoming);
            
            MusicBand stored = CollectionManager.copyWithGeneratedId(incoming, CollectionManager.getNextId());
            boolean added = CollectionManager.addIfMin(stored);
            HistoryParser.addCommand("add_if_min");
            if (added) {
                return ok("Созданный MusicBand успешно добавлен! Его id: " + stored.getId());
            }
            return ok("Созданный MusicBand не был добавлен!");
        } catch (InvalidFieldValueException e) {
            return presentationError(e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
