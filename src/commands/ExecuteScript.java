package commands;

import exceptions.ArgumentExceptionType;
import exceptions.InvalidCommandArgumentException;
import managers.InputManager;
import utility.HistoryParser;
import utility.Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ExecuteScript extends Command {
    private String FILE_NAME;
    private static final ArrayList<String> executingScripts = new ArrayList<>();


    public ExecuteScript(String... parameters) {
        super(parameters);
    }

    @Override
    public String description() {
        return "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.";
    }

    @Override
    public String toString() {
        return "execute_script";
    }

    @Override
    public String paramsToString() {
        return "file_name";
    }

    @Override
    public boolean validate() {
        try {
            if (this.parameters.length != 1) {
                throw new InvalidCommandArgumentException(ArgumentExceptionType.INCORRECT_ARGUMENTS, this);
            }
            FILE_NAME = parameters[0];
            return true;
        } catch (InvalidCommandArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void execute() {
        HistoryParser.addCommand(toString());

        File scriptFile = new File(FILE_NAME);
        String absolutePath;
        try {
            absolutePath = scriptFile.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("Ошибка при получении абсолютного пути файла: " + e.getMessage());
            return;
        }

        if (executingScripts.contains(absolutePath)) {
            System.out.println("Обнаружена рекурсия! Скрипт '" + FILE_NAME + "' уже выполняется.");
            System.out.println("Цепочка вызовов: " + String.join(" -> ", executingScripts) + " -> " + FILE_NAME);
            return;
        }

        if (!scriptFile.exists()) {
            System.out.println("Ошибка: файл '" + FILE_NAME + "' не существует.");
            return;
        }
        if (!scriptFile.canRead()) {
            System.out.println("Ошибка: нет прав на чтение файла '" + FILE_NAME + "'.");
            return;
        }

        Scanner fileScanner = null;
        try {
            executingScripts.add(absolutePath);
            fileScanner = new Scanner(scriptFile);
            InputManager.setScanner(fileScanner); // Устанавливаем файловый сканер
            HistoryParser.changeCanWriteHistory(false);

            System.out.println("Начало выполнения скрипта: " + FILE_NAME);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    Reader.getLine(line);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден во время выполнения: " + e.getMessage());
        } finally {
            InputManager.restoreDefaultScanner(); // Возвращаем сканер консоли
            if (fileScanner != null) {
                fileScanner.close();
            }
            executingScripts.remove(absolutePath);
            HistoryParser.changeCanWriteHistory(true);
            System.out.println("Завершено выполнение скрипта: " + FILE_NAME);
        }
    }
}
