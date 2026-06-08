package utility;

import exceptions.PhysicalException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigLoader {
    private static volatile Map<String, String> dotenv = loadDotenv();
    private static volatile RuntimeConfig runtimeConfig = loadRuntimeConfig();

    private static final int DEFAULT_SERVER_PORT = 5560;
    private static final int DEFAULT_PACKET_CAPACITY = 1024;
    private static final int MIN_PACKET_CAPACITY = 128;
    private static final int MAX_PACKET_CAPACITY = 65507;
    private static final int DEFAULT_SOCKET_TIMEOUT_MS = 0;
    private static final int DEFAULT_SOCKET_RECEIVE_BUFFER_BYTES = 0;
    private static final String SOURCE_DOTENV = ".env";
    private static final String SOURCE_ENV = "System.getenv";
    private static final String SOURCE_DEFAULT = "default";

    public static String getHistoryPath() {
        String historyPath = System.getProperty("user.dir");
        return historyPath + "/history.txt";
    }

    public static String getXMLPath() throws PhysicalException {
        String fileName = getSetting("MUSIC_BANDS_XML_PATH").value();
        if (fileName == null || fileName.isBlank()) {
            throw new PhysicalException("Не задан путь к XML-файлу коллекции (MUSIC_BANDS_XML_PATH)");
        }

        File file = new File(fileName);
        if (!file.exists()) {
            throw new PhysicalException("XML-файл коллекции не существует: " + fileName);
        }
        if (!file.canRead()) {
            throw new PhysicalException("Нет прав на чтение XML-файла коллекции: " + fileName);
        }
        if (!file.canWrite()) {
            System.out.println("Предупреждение: нет прав на запись XML-файла коллекции: " + fileName);
        }
        return fileName;
    }

    public static RuntimeConfig getRuntimeConfig() {
        return runtimeConfig;
    }

    public static RuntimeConfig reloadRuntimeConfig() {
        dotenv = loadDotenv();
        runtimeConfig = loadRuntimeConfig();
        return runtimeConfig;
    }

    public static int resolveServerPort(String[] args) {
        if (args != null && args.length > 0) {
            return Integer.parseInt(args[0]);
        }
        return getRuntimeConfig().serverPort();
    }

    public static String SshPassword() {
        return getSetting("SSH_PASSWORD").value();
    }

    public static String DBPassword() {
        return getSetting("DB_PASSWORD").value();
    }

    private static RuntimeConfig loadRuntimeConfig() {
        IntSetting port = parseIntEnv("MUSIC_BANDS_SERVER_PORT", DEFAULT_SERVER_PORT, 1, 65535);
        IntSetting packetCapacity = parseIntEnv(
                "MUSIC_BANDS_PACKET_CAPACITY",
                DEFAULT_PACKET_CAPACITY,
                MIN_PACKET_CAPACITY,
                MAX_PACKET_CAPACITY
        );
        IntSetting socketTimeoutMs = parseIntEnv("MUSIC_BANDS_SOCKET_TIMEOUT_MS", DEFAULT_SOCKET_TIMEOUT_MS, 0, Integer.MAX_VALUE);
        IntSetting socketReceiveBufferBytes = parseIntEnv(
                "MUSIC_BANDS_SOCKET_RECEIVE_BUFFER_BYTES",
                DEFAULT_SOCKET_RECEIVE_BUFFER_BYTES,
                0,
                Integer.MAX_VALUE
        );
        return new RuntimeConfig(
                port.value(),
                packetCapacity.value(),
                socketTimeoutMs.value(),
                socketReceiveBufferBytes.value(),
                port.source(),
                packetCapacity.source(),
                socketTimeoutMs.source(),
                socketReceiveBufferBytes.source()
        );
    }

    private static IntSetting parseIntEnv(String envName, int defaultValue, int minInclusive, int maxInclusive) {
        SettingValue resolved = getSetting(envName);
        String value = resolved.value();
        if (value == null || value.isBlank()) {
            return new IntSetting(defaultValue, SOURCE_DEFAULT);
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < minInclusive || parsed > maxInclusive) {
                return new IntSetting(defaultValue, SOURCE_DEFAULT);
            }
            return new IntSetting(parsed, resolved.source());
        } catch (NumberFormatException ignored) {
            return new IntSetting(defaultValue, SOURCE_DEFAULT);
        }
    }

    private static SettingValue getSetting(String key) {
        String dotenvValue = dotenv.get(key);
        if (dotenvValue != null && !dotenvValue.isBlank()) {
            return new SettingValue(dotenvValue, SOURCE_DOTENV);
        }
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return new SettingValue(envValue, SOURCE_ENV);
        }
        return new SettingValue(null, SOURCE_DEFAULT);
    }

    private static Map<String, String> loadDotenv() {
        return EnvFileParser.parse(resolveDotenvPath());
    }

    private static Path resolveDotenvPath() {
        String customPath = System.getenv("MUSIC_BANDS_ENV_FILE");
        if (customPath != null && !customPath.isBlank()) {
            return Paths.get(customPath.trim());
        }
        return Paths.get(System.getProperty("user.dir"), ".env");
    }

    private record SettingValue(String value, String source) {
    }

    private record IntSetting(int value, String source) {
    }
}
