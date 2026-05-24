package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Хранит историю введённых команд в памяти и синхронизирует её с файлом.
 */
public class HistoryParser {
    private static final String HISTORY_PATH = ConfigLoader.getHistoryPath();
    private static final ArrayList<String> commandsList = new ArrayList<>();
    private static boolean canWriteHistory = true;

    public static List<String> getCommandsList() {
        return commandsList;
    }

    public static void parseToList() {
        File file = new File(HISTORY_PATH);
        if (file.exists()) {
            try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        commandsList.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
            }
        }
    }

    public static void parseToFile() {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(HISTORY_PATH), StandardCharsets.UTF_8)) {
            for (String command : commandsList) {
                writer.write(command);
                writer.write("\n");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    public static void addCommand(String command) {
        if (canWriteHistory) {
            commandsList.add(command);
            parseToFile();
        }
    }

}
