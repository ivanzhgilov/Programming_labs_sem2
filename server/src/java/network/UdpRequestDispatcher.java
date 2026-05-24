package network;
import protocol.CommandResponse;

import java.io.IOException;
import java.net.DatagramSocket;

public class UdpRequestDispatcher {

    private final UdpRequestProcessor commandProcessor;
    private final UdpResponseSender responseSender;

    public UdpRequestDispatcher(UdpRequestProcessor commandProcessor, UdpResponseSender responseSender) {
        this.commandProcessor = commandProcessor;
        this.responseSender = responseSender;
    }

    public void dispatch(DatagramSocket socket, UdpRequestEnvelope envelope) throws IOException {
        CommandResponse response = commandProcessor.process(envelope);
        responseSender.send(socket, envelope.clientAddress(), response, envelope.messageId());
    }
}
