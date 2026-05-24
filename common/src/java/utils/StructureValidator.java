package utils;

import exceptions.InvalidFieldValueException;
import structs.Coordinates;
import structs.MusicBand;
import structs.Studio;

/**
 * Утилита для валидации структур данных MusicBand.
 */
public class StructureValidator {

    public static void validateCoordinates(Coordinates coords) throws InvalidFieldValueException {
        if (coords == null) {
            throw InvalidFieldValueException.nullValue("coordinates");
        }

        if (coords.getX() > 617) {
            throw InvalidFieldValueException.outOfRange(
                    "coordinates.x",
                    "максимальное значение 617, получено " + coords.getX()
            );
        }

        if (coords.getY() == null) {
            throw InvalidFieldValueException.nullValue("coordinates.y");
        }

        if (coords.getY() <= -370) {
            throw InvalidFieldValueException.outOfRange(
                    "coordinates.y",
                    "значение должно быть больше -370, получено " + coords.getY()
            );
        }
    }

    public static void validateStudio(Studio studio) throws InvalidFieldValueException {
        if (studio == null) {
            throw InvalidFieldValueException.nullValue("studio");
        }

        if (studio.getAddress() == null) {
            throw InvalidFieldValueException.nullValue("studio.address");
        }
    }

    public static void validateMusicBand(MusicBand band) throws InvalidFieldValueException {
        if (band == null) {
            throw InvalidFieldValueException.nullValue("MusicBand");
        }

        if (band.getName() == null) {
            throw InvalidFieldValueException.nullValue("name");
        }

        if (band.getName().isEmpty()) {
            throw InvalidFieldValueException.emptyString("name");
        }

        validateCoordinates(band.getCoordinates());

        if (band.getNumberOfParticipants() == null) {
            throw InvalidFieldValueException.nullValue("numberOfParticipants");
        }

        if (band.getNumberOfParticipants() <= 0) {
            throw InvalidFieldValueException.outOfRange(
                    "numberOfParticipants",
                    "значение должно быть больше 0, получено " + band.getNumberOfParticipants()
            );
        }

        validateStudio(band.getStudio());
    }
}
