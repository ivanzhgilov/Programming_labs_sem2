package protocol;

import structs.MusicBand;
import structs.Studio;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record CommandRequest(
        String commandName,
        List<String> arguments,
        MusicBand bandPayload,
        Studio studioPayload
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
