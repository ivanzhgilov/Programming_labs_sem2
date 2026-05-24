package network;

import protocol.CommandRequest;

import java.net.SocketAddress;

public record UdpRequestEnvelope(SocketAddress clientAddress, CommandRequest request, long messageId) {
}