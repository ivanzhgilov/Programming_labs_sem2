package managers;

import utility.Reader;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputManager {
    private static Scanner currentScanner = new Scanner(System.in);
    public static final InputStream DEFAULT_IN = System.in;

    public static void setScanner(Scanner newScanner) {
        currentScanner = newScanner;
    }

    public static Scanner getScanner() {
        return currentScanner;
    }

    public static void restoreDefaultScanner() {
        currentScanner = new Scanner(System.in);
    }

    /**
     * Читает строку ввода из текущего источника (консоль или файл).
     * Если данные отсутствуют, восстанавливает ввод на стандартную консоль.
     *
     * @return Введенная строка
     */
    public static String readInput() {
        if (!currentScanner.hasNextLine()) {
            restoreStandardInput();
        }
        return currentScanner.nextLine();
    }

    /**
     * Бесконечное чтение строки, пока не получим {@link commands.Exit}
     */
    public static void startInput() {
        try {
            do {
                String consoleInputString = readInput();
                Reader.getLine(consoleInputString);
            } while (true);
        } catch (NoSuchElementException e) {
            System.out.println("Не ломайте плиз");
            System.exit(0);
        }
    }

    /**
     * Восстанавливает стандартный ввод с клавиатуры
     */
    public static void restoreStandardInput() {
        System.setIn(DEFAULT_IN);
        restoreDefaultScanner();
    }
}
