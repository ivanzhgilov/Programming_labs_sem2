package creauters;

import exceptions.InvalidValueFieldException;
import structs.Coordinates;
import managers.InputManager;

public class CoordinatesCreature {
    public static Coordinates creatureCoordinates() {
        boolean flag = true;
        String consoleRead;
        Coordinates.CoordinatesBuilder builder = Coordinates.builder();
        int x;
        do {
            try {
                System.out.print("Введите координату X: ");
                consoleRead = InputManager.getScanner().nextLine();
                x = Integer.parseInt(consoleRead);
                builder.x(x);
                flag = false;
            } catch (NumberFormatException | InvalidValueFieldException e) {
                System.out.println("Координата X должна быть целым числом (" + Integer.MIN_VALUE + " <= x <= 617) и не может быть null");
            }
        } while (flag);


        flag = true;
        double y;
        do {
            try {
                System.out.print("Введите координату Y: ");
                consoleRead = InputManager.getScanner().nextLine();
                y = Double.parseDouble(consoleRead);
                builder.y(y);
                flag = false;
            } catch (NumberFormatException | InvalidValueFieldException e) {
                System.out.println("Координата Y должна быть десятичным числом (-370 < y <= " + Double.MAX_VALUE + ") и не может быть null");
            }
        } while (flag);

        return builder.build();
    }
}
