package main;

import managers.CommandManager;
import managers.InputManager;
import network.ClientRequestDispatcher;
import network.UdpClient;

import java.util.NoSuchElementException;

/**
 * Консольный цикл ввода команд клиента.
 */
public final class ClientConsole {
    private final ClientScriptExecutor scriptExecutor;
    private final ClientPipeline pipeline;

    public ClientConsole(UdpClient udpClient) {
        this.scriptExecutor = new ClientScriptExecutor(this);
        this.pipeline = new ClientPipeline(new CommandManager(scriptExecutor), new ClientRequestDispatcher(udpClient));
    }

    public void run() {
        try {
            while (true) {
                String line = InputManager.readInput();
                if (!processLine(line)) {
                    return;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Произошло прерывание работы программы");
            System.exit(0);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                System.out.println("Не удалось подключиться к серверу, попробуйте позже");
            } else {
                System.out.println("Ошибка клиента: " + e.getMessage());
            }
        }
    }

    /**
     * @return false, если нужно завершить клиент
     */
    public boolean processLine(String line) throws Exception {
        return pipeline.handleLine(line);
    }
}
