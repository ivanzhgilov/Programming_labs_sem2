package commands;

public interface Command {
    CommandResult execute(String[] tokens) throws Exception;
}
