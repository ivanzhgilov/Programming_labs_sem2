package creauters;

import managers.InputManager;

public class AuthInputCreature {
    public static String readNonBlank(String prompt) {
        String input = "";
        while (input.isBlank()) {
            System.out.print(prompt);
            input = InputManager.readInput();
            if (input.isBlank()) {
                System.out.println("Это поле не может быть пустой строкой.");
            }
        }
        return input;
    }

    public static String readLogin() {
        return readNonBlank("Введите логин: ");
    }

    public static String readPassword(String prompt) {
        String password = "";
        while (password.isBlank()) {
            System.out.print(prompt);
            password = InputManager.readPassword();
            if (password.isBlank()) {
                System.out.println("Пароль не может быть пустой строкой.");
            }
        }
        return password;
    }
}
