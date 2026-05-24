package commands.network;

import commands.base.BaseHandler;
import exceptions.InvalidFieldValueException;
import managers.CollectionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utils.StructureValidator;
import structs.Studio;
import utility.HistoryParser;

public class CountGreaterThanStudio extends BaseHandler {
    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            Studio studio = request.studioPayload();
            if (studio == null) {
                return presentationError("Для команды count_greater_than_studio требуется объект Studio.");
            }

            // Валидируем структуру Studio
            StructureValidator.validateStudio(studio);

            long result = CollectionManager.countGreaterThanStudio(studio);
            HistoryParser.addCommand("count_greater_than_studio");
            if (result == 0) {
                return ok("Нет элементов коллекции, у которых значение поля studio больше указанного");
            }
            return ok("Найдено " + result + " элементов");
        } catch (InvalidFieldValueException e) {
            return presentationError(e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
