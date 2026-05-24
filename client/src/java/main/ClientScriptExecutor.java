package main;

import managers.InputManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ClientScriptExecutor {
    private static final Set<String> executingScripts = new HashSet<>();
    private final ClientConsole console;

    public ClientScriptExecutor(ClientConsole console) {
        this.console = console;
    }

    /**
     * @return false, если во время скрипта пришла команда exit
     */
    public boolean execute(String fileName) {
        File scriptFile = new File(fileName);
        String absolutePath;
        try {
            absolutePath = scriptFile.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("Ошибка при получении абсолютного пути файла: " + e.getMessage());
            return true;
        }

        if (executingScripts.contains(absolutePath)) {
            System.out.println("Обнаружена рекурсия! Скрипт '" + fileName + "' уже выполняется.");
            return true;
        }
        if (!scriptFile.exists()) {
            System.out.println("Ошибка: файл '" + fileName + "' не существует.");
            return true;
        }
        if (!scriptFile.canRead()) {
            System.out.println("Ошибка: нет прав на чтение файла '" + fileName + "'.");
            return true;
        }

        try (Scanner fileScanner = new Scanner(scriptFile)) {
            executingScripts.add(absolutePath);
            InputManager.setScanner(fileScanner);
            System.out.println("Начало выполнения скрипта: " + fileName);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!console.processLine(line)) {
                    return false;
                }
            }
            System.out.println("Завершено выполнение скрипта: " + fileName);
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении скрипта: " + e.getMessage());
            return true;
        } finally {
            InputManager.restoreDefaultScanner();
            executingScripts.remove(absolutePath);
        }
    }
}
