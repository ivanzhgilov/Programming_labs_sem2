package main;

import managers.InputManager;
import structs.MusicBand;
import utility.ConfigLoader;
import utility.HistoryParser;
import utility.loader.Loader;

import java.util.HashSet;
import java.util.ArrayList;


/**
 *
 */

public class Main {

    public static HashSet<MusicBand> musicBands = new HashSet<>();
    public static ArrayList<String> commandsList = new ArrayList<>();
    public static String FILE_NAME;

    public static void main(String[] args) {
        FILE_NAME = ConfigLoader.getXMLPath();
        HistoryParser.parseToList();
        Loader.load();
        System.out.println("Программа загружена!\n");
        InputManager.startInput();
    }

}
