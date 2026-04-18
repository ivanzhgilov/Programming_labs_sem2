package utility.loader;

import exceptions.InvalidValueFieldException;
import exceptions.InvalidXMLTagException;
import managers.CollectionManager;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import structs.Coordinates;
import structs.MusicBand;
import structs.MusicGenre;
import structs.Studio;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;

class XMLHandler extends DefaultHandler {
    MusicBand.MusicBandBuilder bandBuilder;
    Coordinates.CoordinatesBuilder coordinatesBuilder;
    Studio.StudioBuilder studioBuilder;
    HashSet<MusicBand> set;
    Integer nextId;
    ZonedDateTime dateInitialization;
    ParserState state;

    static StateTransition[] transitions = new StateTransition[]{
            new StateTransition(null, "root", ParserState.ROOT),

            new StateTransition(ParserState.ROOT, "bands", ParserState.BANDS),

            new StateTransition(ParserState.BANDS, "MusicBand", ParserState.MUSIC_BAND),
            new StateTransition(ParserState.MUSIC_BAND, "id", ParserState.BAND_ID),
            new StateTransition(ParserState.MUSIC_BAND, "name", ParserState.BAND_NAME),
            new StateTransition(ParserState.MUSIC_BAND, "coordinates", ParserState.COORDINATES),
            new StateTransition(ParserState.MUSIC_BAND, "creationDate", ParserState.BAND_DATE_OF_CREATION),
            new StateTransition(ParserState.MUSIC_BAND, "numberOfParticipants", ParserState.BAND_NUMBER_OF_PARTICIPANTS),
            new StateTransition(ParserState.MUSIC_BAND, "genre", ParserState.MUSIC_GENRE),

            new StateTransition(ParserState.COORDINATES, "x", ParserState.COORDINATE_X),
            new StateTransition(ParserState.COORDINATES, "y", ParserState.COORDINATE_Y),

            new StateTransition(ParserState.MUSIC_BAND, "studio", ParserState.BAND_STUDIO),
            new StateTransition(ParserState.BAND_STUDIO, "address", ParserState.STUDIO_ADDRESS),

            new StateTransition(ParserState.ROOT, "ServiceData", ParserState.SERVICE_DATA),
            new StateTransition(ParserState.SERVICE_DATA, "nextId", ParserState.NEXT_ID),
            new StateTransition(ParserState.SERVICE_DATA, "dateInitialization", ParserState.DATE_INITIALIZATION),
    };

    XMLHandler(HashSet<MusicBand> set) {
        this.set = set;
        this.bandBuilder = null;
        this.studioBuilder = null;
        this.coordinatesBuilder = null;
        this.state = null;
        this.dateInitialization = null;
        this.nextId = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            for (StateTransition transition : transitions) {
                if (transition.prev_state() != state || !transition.qName().equals(qName))
                    continue;

                state = transition.new_state();

                if (state == ParserState.MUSIC_BAND)
                    bandBuilder = MusicBand.builder();
                else if (state == ParserState.COORDINATES)
                    coordinatesBuilder = Coordinates.builder();
                else if (state == ParserState.BAND_STUDIO)
                    studioBuilder = Studio.builder();

                return;
            }
            throw new InvalidXMLTagException(qName);
        } catch (InvalidXMLTagException e) {
            System.out.println("Ошибка при чтении:");
            System.out.println(e.getMessage());
            System.out.println("Исправьте файл и перезагрузите программу");
            System.exit(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        try {
            for (StateTransition transition : transitions) {
                if (transition.new_state() != state || !transition.qName().equals(qName))
                    continue;

                state = transition.prev_state();

                if (transition.new_state() == ParserState.MUSIC_BAND) {
                    set.add(bandBuilder.build());
                    bandBuilder = null;
                } else if (transition.new_state() == ParserState.COORDINATES) {
                    bandBuilder.coordinates(coordinatesBuilder.build());
                    coordinatesBuilder = null;
                } else if (transition.new_state() == ParserState.BAND_STUDIO) {
                    bandBuilder.studio(studioBuilder.build());
                    studioBuilder = null;
                } else if (transition.new_state() == ParserState.ROOT) {
                    CollectionManager.setNextId(nextId);
                    CollectionManager.setDateInitialization(dateInitialization);
                }

                return;
            }
            throw new InvalidXMLTagException(qName);
        } catch (InvalidXMLTagException e) {
            System.out.println("Ошибка при чтении:");
            System.out.println(e.getMessage());
            System.out.println("Исправьте файл и перезагрузите программу");
            System.exit(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String str = new String(ch, start, length);

        try {
            switch (state) {
                case BAND_ID:
                    bandBuilder.id(Integer.parseInt(str));
                    break;
                case BAND_NAME:
                    bandBuilder.name(str);
                    break;
                case COORDINATE_X:
                    coordinatesBuilder.x(Integer.parseInt(str));
                    break;
                case COORDINATE_Y:
                    coordinatesBuilder.y(Double.parseDouble(str));
                    break;
                case BAND_DATE_OF_CREATION:
                    bandBuilder.creationDate(Date.from(Instant.parse(str)));
                    break;
                case BAND_NUMBER_OF_PARTICIPANTS:
                    bandBuilder.numberOfParticipants(Integer.parseInt(str));
                    break;
                case MUSIC_GENRE:
                    bandBuilder.genre(MusicGenre.valueOf(str));
                    break;
                case STUDIO_ADDRESS:
                    studioBuilder.address(str);
                    break;
                case NEXT_ID:
                    nextId = Integer.parseInt(str);
                    break;
                case DATE_INITIALIZATION:
                    dateInitialization = ZonedDateTime.parse(str);
                    break;
                default:
            }
        } catch (InvalidValueFieldException | NumberFormatException e) {
            System.out.println("Ошибка при чтении данных");
            System.out.println("Некоторые поля не валидны, исправьте файл и перезагрузите программу");
            System.exit(0);
        }
    }
}
