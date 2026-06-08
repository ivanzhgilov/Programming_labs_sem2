package main;

import commands.CommandResult;
import managers.CommandManager;
import network.ClientRequestDispatcher;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Клиентский pipeline: parse/command -> request -> dispatch.
 */
public class ClientPipeline {
    private final CommandManager commandManager;
    private final ClientRequestDispatcher requestDispatcher;

    public ClientPipeline(CommandManager commandManager, ClientRequestDispatcher requestDispatcher) {
        this.commandManager = commandManager;
        this.requestDispatcher = requestDispatcher;
    }

    /**
     * @return false, если нужно завершить клиент
     */
    public boolean handleLine(String line) throws Exception {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return true;
        }

        String commandName = trimmed.split(" ")[0].toLowerCase();

        if (!managers.AuthGuard.canExecute(commandName)) {
            return true;
        }

        CommandResult result = commandManager.processLine(line);
        return requestDispatcher.dispatch(result);
    }
}
