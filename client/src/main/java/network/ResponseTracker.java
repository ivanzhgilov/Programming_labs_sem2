package network;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import protocol.CommandResponse;

public class ResponseTracker {
    private final ConcurrentHashMap<Long, CompletableFuture<CommandResponse>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<CommandResponse> track(long messageId) {
        CompletableFuture<CommandResponse> future = new CompletableFuture<>();
        pendingRequests.put(messageId, future);
        return future;
    }

    public void complete(long messageId, CommandResponse response) {
        CompletableFuture<CommandResponse> future = pendingRequests.remove(messageId);
        if (future != null) {
            future.complete(response);
        }
    }
}
