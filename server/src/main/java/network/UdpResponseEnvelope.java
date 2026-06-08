package network;

import java.net.SocketAddress;
import protocol.CommandResponse;

public record UdpResponseEnvelope(
    SocketAddress clientAddress,
    CommandResponse response,
    long messageId
) {}
