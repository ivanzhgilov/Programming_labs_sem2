package utility.loader;

import main.Main;
import managers.CollectionManager;
import org.xml.sax.SAXException;
import structs.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;


public class Loader {
    public static void load() {
        boolean flag = true;
        try {
            BufferedReader br;
            br = new BufferedReader(new FileReader(Main.FILE_NAME));
            if (br.readLine() == null) {
                CollectionManager.setNextId(0);
                CollectionManager.setDateInitialization(null);
                flag = false;
            }
        } catch (IOException e) {
            System.out.println();
        }
        HashSet<MusicBand> set = Main.musicBands;
        if (flag) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(Main.FILE_NAME, new XMLHandler(set));
            } catch (ParserConfigurationException | SAXException | IOException e) {
                System.out.println("Что-то не так с XML файлом. Исправьте и перезапустите программу");
                System.exit(0);
            }
        }
    }

    public static void save() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(Main.FILE_NAME), StandardCharsets.UTF_8);

            writer.write("<root>");

            writer.write("<bands>");

            for (MusicBand band : Main.musicBands) {
                writer.write("<MusicBand>");
                writer.write(String.format("<id>%d</id>", band.getId()));
                writer.write(String.format("<name>%s</name>", band.getName()));
                writer.write("<coordinates>");
                writer.write(String.format("<x>%s</x>", band.getCoordinates().getX()));
                writer.write(String.format("<y>%s</y>", band.getCoordinates().getY()));
                writer.write("</coordinates>");
                writer.write(String.format("<creationDate>%s</creationDate>", band.getCreationDate().toInstant()));
                writer.write(String.format("<numberOfParticipants>%s</numberOfParticipants>", band.getNumberOfParticipants()));
                if (band.getGenre() != null)
                    writer.write(String.format("<genre>%s</genre>", band.getGenre().name()));
                if (band.getStudio() != null) {
                    writer.write("<studio>");
                    writer.write(String.format("<address>%s</address>", band.getStudio().getAddress()));
                    writer.write("</studio>");
                }
                writer.write("</MusicBand>");
            }

            writer.write("</bands>");

            writer.write("<ServiceData>");
            writer.write(String.format("<nextId>%d</nextId>", CollectionManager.getNextId()));
            writer.write(String.format("<dateInitialization>%s</dateInitialization>", CollectionManager.getDateInitialization()));
            writer.write("</ServiceData>");

            writer.write("</root>");

            writer.close();
            System.out.println("Коллекция сохранена в файл");
        } catch (IOException e) {
            System.out.printf("Ошибка записи в файл: %s\n", e.getLocalizedMessage());
        }
    }
}


