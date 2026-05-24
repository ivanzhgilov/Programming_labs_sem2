package utility.loader;

import exceptions.PhysicalException;
import managers.CollectionManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import structs.MusicBand;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

public class Loader {
    private static String fileName;

    public static void setFileName(String fileName) {
        Loader.fileName = fileName;
    }

    private static String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static void load() throws PhysicalException {
        boolean hasContent = true;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            if (reader.readLine() == null) {
                CollectionManager.setNextId(0);
                CollectionManager.setDateInitialization(null);
                hasContent = false;
            }
        } catch (IOException e) {
            throw new PhysicalException("Не удалось прочитать файл коллекции: " + e.getMessage(), e);
        }

        HashSet<MusicBand> set = CollectionManager.getSet();
        if (!hasContent) {
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(fileName), StandardCharsets.UTF_8)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            InputSource source = new InputSource(reader);
            source.setEncoding(StandardCharsets.UTF_8.name());
            saxParser.parse(source, new XMLHandler(set));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new PhysicalException("Не удалось загрузить XML-коллекцию: " + e.getMessage(), e);
        }
    }

    public static void save() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write("<root>");
            writer.write("<bands>");

            for (MusicBand band : CollectionManager.getSet()) {
                writer.write("<MusicBand>");
                writer.write(String.format("<id>%d</id>", band.getId()));
                writer.write(String.format("<name>%s</name>", escapeXml(band.getName())));
                writer.write("<coordinates>");
                writer.write(String.format("<x>%s</x>", band.getCoordinates().getX()));
                writer.write(String.format("<y>%s</y>", band.getCoordinates().getY()));
                writer.write("</coordinates>");
                writer.write(String.format("<creationDate>%s</creationDate>", band.getCreationDate().toInstant()));
                writer.write(String.format("<numberOfParticipants>%s</numberOfParticipants>", band.getNumberOfParticipants()));
                if (band.getGenre() != null) {
                    writer.write(String.format("<genre>%s</genre>", band.getGenre().name()));
                }
                if (band.getStudio() != null) {
                    writer.write("<studio>");
                    writer.write(String.format("<address>%s</address>", escapeXml(band.getStudio().getAddress())));
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
