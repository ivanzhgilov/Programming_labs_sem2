package creauters;

import exceptions.InvalidValueFieldException;
import managers.InputManager;

public class IdCreature {
    static public int createId() {
        int id=0;
        boolean flag = true;
        String consoleRead;
        do {
            try {
                System.out.println("Введите id: ");
                consoleRead = InputManager.readInput();
                id = Integer.parseInt(consoleRead);
                if (id <= 0) {
                    throw new InvalidValueFieldException("");
                }
                flag = false;
            } catch (NumberFormatException | InvalidValueFieldException e) {
                System.out.println("id должно быть целым положительным числом");
            }
        } while (flag);
        return id;
    }
}
