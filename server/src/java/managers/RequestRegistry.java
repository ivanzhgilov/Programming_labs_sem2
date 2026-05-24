package managers;

import commands.base.Handler;
import commands.network.*;
import exceptions.CommandNotFoundException;
import protocol.CommandRequest;
import protocol.CommandResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestRegistry {
    private final Map<String, Handler> handlers = new HashMap<>();

    public RequestRegistry() {
        handlers.put("help", new Help());
        handlers.put("info", new Info());
        handlers.put("show", new Show());
        handlers.put("print_descending", new PrintDescending());
        handlers.put("history", new History());
        handlers.put("clear", new Clear());
        handlers.put("add", new Add());
        handlers.put("add_if_min", new AddIfMin());
        handlers.put("update", new Update());
        handlers.put("count_greater_than_studio", new CountGreaterThanStudio());
        handlers.put("remove_by_id", new RemoveById());
        handlers.put("remove_any_by_number_of_participants", new RemoveAnyByNumberOfParticipants());
        handlers.put("remove_lower", new RemoveLower());
    }

    /**
     * Выполняет команду или выбрасывает исключение если команда не найдена.
     */
    public CommandResponse execute(CommandRequest request) throws CommandNotFoundException {
        Handler handler = handlers.get(request.commandName());
        if (handler == null) {
            throw new CommandNotFoundException(request.commandName());
        }
        return handler.execute(request);
    }
}
