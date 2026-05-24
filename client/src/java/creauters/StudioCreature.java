package creauters;

import exceptions.InvalidValueFieldException;
import managers.InputManager;
import structs.MusicBand;
import structs.Studio;

/**
 * Класс для инициализации пользователем поля studio {@link MusicBand}
 */
public class StudioCreature {

    public static Studio creatureStudio() {
        Studio.StudioBuilder builder = new Studio.StudioBuilder();
        boolean flag = true;
        String consoleRead;
        do {
            try {
                System.out.print("Введите адрес студии: ");
                consoleRead = InputManager.getScanner().nextLine();
                if(!consoleRead.isEmpty()){
                    builder.address(consoleRead);
                    flag = false;
                }
            } catch (InvalidValueFieldException e) {
                System.out.println(e.getMessage());
            }
        } while (flag);
        try {
            return builder.build();
        } catch (InvalidValueFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
