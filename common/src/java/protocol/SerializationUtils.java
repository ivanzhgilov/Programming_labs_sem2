package protocol;

import java.io.*;

public final class SerializationUtils {
    private SerializationUtils() {
    }

    public static byte[] serialize(Serializable obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> type) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data); ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object value = ois.readObject();
            return type.cast(value);
        }
    }
}
