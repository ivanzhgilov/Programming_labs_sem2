package utility;

/**
 * Управляет переменной окружения для парсинга history
 */

public class ConfigLoader {

    /**
     *
     *
     */

    public static String getHistoryPath() {
        String historyPath = System.getenv("MUSIC_BANDS_HISTORY");
        return "";
    }
}
