package network;

import commands.CommandResult;
import exceptions.ErrorLevel;
import protocol.CommandResponse;

public class ClientRequestDispatcher {
    private final UdpClient udpClient;

    public ClientRequestDispatcher(UdpClient udpClient) {
        this.udpClient = udpClient;
    }

    /**
     * @return false, если нужно завершить клиента
     */
    public boolean dispatch(CommandResult result) throws Exception {
        if (!result.continueClient()) {
            return false;
        }
        if (result.request() == null) {
            return true;
        }
        CommandResponse response = udpClient.send(result.request());
        handleResponse(response);
        return true;
    }

    private void handleResponse(CommandResponse response) {
        if (response.success()) {
            System.out.println(response.message());
        } else {
            ErrorLevel level = response.errorLevel();
            
            if (level == ErrorLevel.PHYSICAL) {
                System.out.println("[Ошибка] На сервере произошла ошибка. Попробуйте позже.");
            } else if (level != null) {
                System.out.printf("[%s] %s%n", level.getDescription(), response.message());
            } else {
                System.out.println("[Ошибка] " + response.message());
            }
        }
    }
}
