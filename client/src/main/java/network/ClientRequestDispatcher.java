package network;

import commands.CommandResult;
import exceptions.ErrorLevel;
import managers.SessionManager;
import protocol.CommandRequest;
import protocol.CommandResponse;
import utility.CryptoUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ClientRequestDispatcher {
    private final UdpClient udpClient;
    static private final int TIMEOUT = 30;

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

        try {
            CommandRequest request = result.request();
            CompletableFuture<CommandResponse> future = udpClient.send(request);
            CommandResponse response = future.get(TIMEOUT, TimeUnit.SECONDS);

            handleResponse(response);

            if (response.success()) {
                String cmd = request.commandName();
                if ("login".equals(cmd) || "register".equals(cmd)) {

                    String login = CryptoUtils.decrypt(request.login());
                    String password = CryptoUtils.decrypt(request.password());
                    int lastPipeIndex = login.lastIndexOf('|');
                    String login_val = login.substring(0, lastPipeIndex);
                    lastPipeIndex = password.lastIndexOf('|');
                    String password_val = login.substring(0, lastPipeIndex);
                    SessionManager.setSession(login_val, password_val);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (ExecutionException e) {
            handleResponse(CommandResponse.error("Ошибка при выполнении запроса: " + e.getCause().getMessage(), ErrorLevel.SERVER));
        } catch (TimeoutException e) {
            handleResponse(CommandResponse.error("Сервер не ответил в течение " + TIMEOUT + " секунд.", ErrorLevel.SERVER));
        }

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
