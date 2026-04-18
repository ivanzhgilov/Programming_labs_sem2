package creauters;

import exceptions.InvalidValueFieldException;
import structs.Coordinates;
import structs.MusicBand;
import structs.MusicGenre;
import structs.Studio;
import managers.InputManager;


public class MusicBandCreature {
    public static MusicBand creatureMusicBand(MusicBand.MusicBandBuilder builder) {
        boolean flag = true;
        String consoleRead;
        do {
            try {
                System.out.print("Введите имя группы: ");
                consoleRead = InputManager.getScanner().nextLine();
                builder.name(consoleRead);
                flag = false;
            } catch (InvalidValueFieldException e) {
                System.out.println(e.getMessage());
            }
        } while (flag);

        Coordinates coordinates = CoordinatesCreature.creatureCoordinates();
        builder.coordinates(coordinates);

        flag = true;
        int numberOfParticipants;
        do {
            try {
                System.out.print("Введите количество членов группы: ");
                consoleRead = InputManager.getScanner().nextLine();
                numberOfParticipants = Integer.parseInt(consoleRead);
                builder.numberOfParticipants(numberOfParticipants);
                flag = false;
            } catch (NumberFormatException | InvalidValueFieldException e) {
                System.out.println("numberOfParticipants должно быть целым положительным числом меньшим " + Integer.MAX_VALUE);
            }
        } while (flag);

        MusicGenre genre = MusicGenreCreature.createMusicGenre();
        builder.genre(genre);

        System.out.println("Введите пустую строку (нажмите enter), если хотите оставить поле studio пустым");
        System.out.println("Иначе введите любой символ");
        consoleRead = InputManager.getScanner().nextLine();
        if (consoleRead == null || consoleRead.isEmpty()) {
            builder.studio(null);
        } else {
            Studio studio = StudioCreature.creatureStudio();
            builder.studio(studio);
        }

        return builder.build();
    }
}
