package commands;

import javax.xml.bind.JAXB;


/**
 * Класс-предок для всех команд пакета commands, тут обрабатываются всё, что ожидалось как команда, но среди команд этого нет
 */

public class Command {

    /**
     * Параметр команды: для update это id, для add - element и тд. В общем обязательная часть синтаксиса какой-то команды
     * <p></p>
     * Если у команды нет параметра, то необходимо отправить в конструктор null
     */
    public Object parameter;

    public Command(Object parameter) {
        this.parameter = parameter;
    }

    public boolean validate() {
        return true;
    }

    public void execute() {
        System.out.println("Такой команды нет! Используйте help чтобы посмотреть список команд");
    }

    public String description() {
        return "";
    }
}
