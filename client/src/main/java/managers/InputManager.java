package managers;

import java.io.InputStream;
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
     * Читает пароль, скрывая ввод в консоли, если это возможно.
     *
     * @return Введенный пароль
     */
    public static String readPassword() {
        java.io.Console console = System.console();
        if (console == null) {
            // Если консоль недоступна (например, запуск из IDE), возвращаемся к обычному Scanner
            return readInput();
        }
        char[] passwordChars = console.readPassword();
        return new String(passwordChars);
    }

    /**
     * Восстанавливает стандартный ввод с клавиатуры
     */
    public static void restoreStandardInput() {
        System.setIn(DEFAULT_IN);
        restoreDefaultScanner();
    }
}
