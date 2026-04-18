package utility;

import main.Main;

import java.io.*;
import java.util.List;

/**
 * Сериализует в {@link Main#commandsList} / десериализует в файлик .txt (для хранения истории за предыдущие сеансы) предыдущие команды юзера
 *
 * @see commands.History
 */

public class HistoryParser {
    private static final String HISTORY_PATH = ConfigLoader.getHistoryPath();
    private static boolean canWriteHistory = true;

    /**
     * Десериализация в {@link Main#commandsList}
     */
    public static void parseToList() {
        File file = new File(HISTORY_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        Main.commandsList.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
            }
        }
    }

    /**
     * Парсим из {@link Main#commandsList} в файл-буфер
     */
    public static void parseToFile() {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(HISTORY_PATH))) {
            List<String> commands = Main.commandsList;
            for (String command : commands) {
                writer.write(command);
                writer.write("\n");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    public static void addCommand(String command) {
        if (canWriteHistory) {
            Main.commandsList.add(command);
            parseToFile();
        }
    }

    public static void changeCanWriteHistory(boolean canWriteHistory) {
        HistoryParser.canWriteHistory = canWriteHistory;
    }
}