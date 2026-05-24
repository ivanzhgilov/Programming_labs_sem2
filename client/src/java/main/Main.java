package main;

import network.UdpClient;

/**
 * Главная точка входа клиентского приложения.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        String host = resolveHost(args);
        int port = resolvePort(args);
        UdpClient client = new UdpClient(host, port);
        System.out.println("Программа загружена!\n");
        new ClientConsole(client).run();
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
