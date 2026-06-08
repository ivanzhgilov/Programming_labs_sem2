package commands.network;

import commands.base.BaseHandler;
import managers.AuthService;
import managers.SecurityVerifier;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.HistoryParser;
import utility.UserContext;

import java.util.List;
import java.util.stream.Collectors;

public class Help extends BaseHandler {
    private static final List<String> COMMAND_INFO = List.of(
            "help: вывести справку по доступным командам",
            "info: вывести информацию о коллекции",
            "show: вывести все элементы коллекции",
            "add {element}: добавить новый элемент в коллекцию",
            "update id {element}: обновить элемент по id",
            "remove_by_id id: удалить элемент по id",
            "clear: очистить коллекцию",
            "execute_script file_name: считать и исполнить скрипт из файла",
            "exit: завершить клиент",
            "add_if_min {element}: добавить элемент, если он минимальный",
            "remove_lower: удалить элементы меньше заданного",
            "history: вывести последние 8 команд",
            "remove_any_by_number_of_participants number: удалить любой элемент по numberOfParticipants",
            "count_greater_than_studio studio: посчитать элементы с studio больше заданного",
            "print_descending: вывести элементы в порядке убывания",
            "login: войти в аккаунт",
            "register: зарегистрировать пользователя",
            "logout: выйти из аккаунта"
    );

    @Override
    public CommandResponse execute(CommandRequest request) {
        try {
            int ownerId = UserContext.getUserId();

            HistoryParser.addCommandForUser(ownerId, "help");

            String responseText = COMMAND_INFO.stream()
                    .sorted()
                    .collect(Collectors.joining("\n"));

            return ok(responseText);

        } catch (SecurityException e) {
            return presentationError("Ошибка безопасности при дешифровании пакета данных: " + e.getMessage());
        } catch (Exception e) {
            return physicalError("Внутренняя ошибка сервера при выполнении help: " + e.getMessage());
        }
    }
}