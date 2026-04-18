package creauters;

import structs.MusicGenre;
import managers.InputManager;


public class MusicGenreCreature {
    public static MusicGenre createMusicGenre() {
        MusicGenre result = null;
        boolean flag = true;
        String consoleRead;
        do {
            try{
                System.out.println("Введите жанр музыки группы (один из вариантов: " + MusicGenre.getAllGenre() + ")");
                System.out.println("Введите пустую строку (нажмите enter), если хотите оставить это поле пустым");
                consoleRead = InputManager.getScanner().nextLine();
                if (!consoleRead.isEmpty()){
                    result = MusicGenre.valueOf(consoleRead);
                }
                flag = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Такого значения нет!");
            }
        } while (flag);
        return result;
    }
}
