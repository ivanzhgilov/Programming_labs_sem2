package utility;


public record RuntimeConfig(
        int serverPort,
        int packetCapacityBytes,
        int socketTimeoutMs,
        int socketReceiveBufferBytes,
        String serverPortSource,
        String packetCapacitySource,
        String socketTimeoutSource,
        String socketReceiveBufferSource
) {
    public RuntimeConfig {
        if (serverPort < 1 || serverPort > 65535) {
            throw new IllegalArgumentException("serverPort должен быть в диапазоне [1..65535]");
        }
    }
}

