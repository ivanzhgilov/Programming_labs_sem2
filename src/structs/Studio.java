package structs;

import java.util.Comparator;

import exceptions.InitializationException;
import exceptions.InitializationExceptionType;
import exceptions.InvalidValueFieldException;

/**
 * Студия - одно из полей {@link MusicBand}
 *
 * @see Coordinates
 * @see MusicGenre
 */


public class Studio {
    private final String address; //Поле не может быть null

    private Studio(StudioBuilder builder) {
        try {
            if (builder.address == null) {
                throw new InitializationException(InitializationExceptionType.NULL);
            }
        } catch (InitializationException e) {
            System.out.println(e.getMessage());
        }
        this.address = builder.address;
    }

    public String getAddress() {
        return address;
    }

    /**
     * @return билдер класса Studio
     */

    public static StudioBuilder builder() {
        return new StudioBuilder();
    }

    /**
     * Билдер для инициализации объекта Studio
     */
    public static class StudioBuilder {
        private String address;

        public StudioBuilder() {
            super();
        }

        /**
         * Инициализация поля address
         *
         * @param address адрес студии
         */
        public StudioBuilder address(String address) throws InvalidValueFieldException {
            if (address.isEmpty()) {
                throw new InvalidValueFieldException("Адрес не может быть пустым");
            }
            this.address = address;
            return this;
        }

        /**
         * @return Готовый объект Studio
         */
        public Studio build() {
            return new Studio(this);
        }
    }

    public static class ComparatorStudio implements Comparator<Studio> {

        @Override
        public int compare(Studio o1, Studio o2) {
            return o1.getAddress().compareTo(o2.getAddress());
        }
    }

    @Override
    public String toString() {
        return "адрес: " + address;
    }

}
