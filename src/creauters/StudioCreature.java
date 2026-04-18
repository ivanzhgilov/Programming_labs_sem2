package creauters;

import exceptions.InvalidValueFieldException;
import structs.MusicBand;
import structs.Studio;
import managers.InputManager;

/**
 * Класс для инициализации пользователем поля studio {@link MusicBand}
 */

public class StudioCreature {

    /**
     * @return Созданный объект пользователем
     */
    public static Studio creatureStudio() {
        Studio.StudioBuilder builder = new Studio.StudioBuilder();
        boolean flag = true;
        String consoleRead;
        do {
            try {
                System.out.print("Введите адрес студии: ");
                consoleRead = InputManager.getScanner().nextLine();
                builder.address(consoleRead);
                flag = false;
            } catch (InvalidValueFieldException e) {
                System.out.println(e.getMessage());
            }
        } while (flag);
        return builder.build();
    }
}
