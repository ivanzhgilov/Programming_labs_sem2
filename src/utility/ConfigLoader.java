package utility;

import java.io.File;

/**
 *
 */

public class ConfigLoader {
    /**
     * Возвращает путь до history.txt
     */

    public static String getHistoryPath() {
        String historyPath = System.getProperty("user.dir");
        return historyPath + "/history.txt";
    }

    public static String getXMLPath() {
        String fileName = System.getenv("MUSIC_BANDS_XML_PATH");
        if (fileName == null) {
            System.out.println("Перезагрузите программу!\nУкажите путь до файла хранилища в переменной окружения MUSIC_BANDS_XML_PATH");
            System.exit(0);
        }
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Ошибка: файл не существует");
            System.exit(0);
        }
        if (!file.canRead()) {
            System.out.println("Нет прав для чтения файла, выдайте права для чтения и перезапустите программу!");
            System.exit(0);
        }
        if (!file.canWrite()) {
            System.out.println("Предупреждение: запись в файл невозможна, выдайте права на запись, если хотите сохранить изменения!");
        }
        return fileName;
    }
}