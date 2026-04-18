package commands;


/**
 * Класс-предок для всех команд пакета commands, тут обрабатываются всё, что ожидалось как команда, но среди команд этого нет
 */

public class Command {

    /**
     * Параметр команды: для update это id, для add - element и тд. В общем обязательная часть синтаксиса какой-то команды
     * <p></p>
     * Если у команды нет параметра, то необходимо отправить в конструктор null
     */
    public String[] parameters;

    public Command(String... parameters) {
        this.parameters = parameters;
    }

    /**
     * Проверка валидности параметров, введенных пользователем, для конкретной команды
     */
    public boolean validate() {
        return true;
    }

    /**
     * Выполнение того, что должна делать команда
     */
    public void execute() {
        System.out.println("Такой команды нет! Используйте help чтобы посмотреть список команд");
    }

    @Override
    public String toString() {
        return "command";
    }

    public String paramsToString() {
        return "";
    }

    public String description() {
        return ":базовая команда";
    }

    /**
     *
     * @return описание команды в виде имя_команды параметры: описание, что делает команда, нужно для help,
     */

    public String commandInfo() {
        String space = " ";
        if(paramsToString().isEmpty()) {
            space = "";
        }
        return this + space + paramsToString() + ": " + description();
    }
}
