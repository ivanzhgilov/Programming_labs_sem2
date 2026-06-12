package main;

import network.UdpClient;
import managers.NetworkManager;
import javafx.application.Application;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        String host = resolveHost(args);
        int port = resolvePort(args);

        String mode = System.getProperty("mode", "gui");

        if ("cli".equalsIgnoreCase(mode)) {
            runConsoleMode(host, port);
        } else {
            runGuiMode(host, port);
        }
    }

    private static void runConsoleMode(String host, int port) {
        UdpClient client = null;
        try {
            client = new UdpClient(host, port);
            System.out.println("Программа загружена в консольном режиме!");

            new ClientConsole(client).run();

        } catch (Exception e) {
            System.err.println("Критическая ошибка в работе клиента: " + e.getMessage());
            System.exit(1);
        } finally {
            if (client != null) {
                try {
                    client.close();
                    System.out.println("Сетевое соединение успешно закрыто. До свидания!");
                } catch (Exception e) {
                    System.err.println("Ошибка при освобождении сетевых ресурсов: " + e.getMessage());
                }
            }
        }
    }

    private static void runGuiMode(String host, int port) {
        // Инициализируем сетевой менеджер перед запуском JavaFX
        NetworkManager.getInstance().init(host, port);

        // Запуск JavaFX приложения
        Application.launch(GuiClientApplication.class);
    }

    private static String resolveHost(String[] args) {
        if (args != null && args.length > 0) {
            return args[0];
        }
        String envHost = System.getenv("MUSIC_BANDS_SERVER_HOST");
        if (envHost != null && !envHost.isBlank()) {
            return envHost;
        }
        return "127.0.0.1";
    }

    private static int resolvePort(String[] args) {
        if (args != null && args.length > 1) {
            return Integer.parseInt(args[1]);
        }
        String envPort = System.getenv("MUSIC_BANDS_SERVER_PORT");
        if (envPort != null && !envPort.isBlank()) {
            return Integer.parseInt(envPort);
        }
        return 5560;
    }
}
