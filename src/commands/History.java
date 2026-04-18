package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import main.Main;
import utility.HistoryParser;

public class History extends Command {
    private final int endIndex;

    public History(String... parameters) {
        super(parameters);
        this.endIndex = Math.max(0, Main.commandsList.size() - 8);
    }

    @Override
    public String description() {
        return "вывести последние 8 команд (без их аргументов)";
    }

    public String toString() {
        return "history";
    }

    @Override
    public void execute() {
        System.out.println();
        for (int i = Main.commandsList.size() - 1; i >= this.endIndex; --i) {
            System.out.println(Main.commandsList.get(i));
        }
        System.out.println();
        HistoryParser.addCommand(toString());
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameters.length > 0) {
                throw new InvalidCommandArgumentException(ArgumentExceptionType.NOT_NULL_ARGUMENT, this);
            }
            return true;
        } catch (InvalidCommandArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
