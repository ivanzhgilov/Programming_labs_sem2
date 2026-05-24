package protocol;

import java.io.Serializable;

public record PacketChunk(
        long messageId,
        int index,
        int total,
        byte[] data
) implements Serializable {}