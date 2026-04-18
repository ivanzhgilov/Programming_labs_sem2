package structs;

import exceptions.InitializationException;
import exceptions.InitializationExceptionType;
import exceptions.InvalidValueFieldException;
import main.Main;
import managers.CollectionManager;


import java.util.Date;
import java.util.Objects;


/**
 * Основной класс данных, все его объекты хранятся в {@link Main#musicBands}
 */
public class MusicBand implements Comparable<MusicBand> {
    private final int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; //Поле не может быть null
    private final java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final Integer numberOfParticipants; //Поле не может быть null, Значение поля должно быть больше 0
    private final MusicGenre genre; //Поле может быть null
    private final Studio studio; //Поле не может быть null

    private MusicBand(MusicBandBuilder builder) {
        try {

            if (builder.name == null || builder.coordinates == null || builder.numberOfParticipants == null) {
                throw new InitializationException(InitializationExceptionType.NULL);
            }
        } catch (InitializationException e) {
            System.err.println(e.getMessage());
        }
        if (builder.id == 0) {
            this.id = CollectionManager.getNextId();
        } else {
            this.id = builder.id;
        }
        this.name = builder.name;
        this.coordinates = builder.coordinates;
        this.creationDate = Objects.requireNonNullElseGet(builder.creationDate, Date::new);
        this.numberOfParticipants = builder.numberOfParticipants;
        this.genre = builder.genre;
        this.studio = builder.studio;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Studio getStudio() {
        return studio;
    }

    @Override
    public int compareTo(MusicBand other) {
        return this.id - other.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MusicBand other) {
            return this.id == other.id;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: ").append(id).append("\n");
        builder.append("name: ").append(name).append("\n");
        builder.append("coordinates: ").append(coordinates).append("\n");
        builder.append("creationDate: ").append(creationDate).append("\n");
        builder.append("numberOfParticipants: ").append(numberOfParticipants).append("\n");
        builder.append("genre: ");
        if (genre != null) {
            builder.append(genre).append("\n");
        } else {
            builder.append("не указано\n");
        }
        builder.append("studio: ");
        if (studio != null) {
            builder.append(studio).append("\n");
        } else {
            builder.append("не указано\n");
        }
        return builder.toString();
    }

    /**
     * @return Билдер для инициализации полей
     */
    public static MusicBandBuilder builder() {
        return new MusicBandBuilder();
    }

    /**
     * Класс-билдер для создания объекта {@link MusicBand}
     */


    public static class MusicBandBuilder {
        private int id;
        private String name;
        private Coordinates coordinates;
        private java.util.Date creationDate;
        private Integer numberOfParticipants;
        private MusicGenre genre;
        private Studio studio;

        public MusicBandBuilder() {
            super();
        }

        /**
         * Инициализация id
         *
         * @param id id группы
         * @return Билдер для дальнейшей инициализации
         */

        public MusicBandBuilder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Инициализация name
         *
         * @param name имя группы
         * @return Билдер для дальнейшей инициализации
         */

        public MusicBandBuilder name(String name) throws InvalidValueFieldException {
            if (name == null || name.isEmpty()) {
                throw new InvalidValueFieldException("Поле name должно быть отличным от пустой строки!");
            }
            this.name = name;
            return this;
        }

        public MusicBandBuilder coordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public MusicBandBuilder creationDate(java.util.Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public MusicBandBuilder numberOfParticipants(Integer numberOfParticipants) throws InvalidValueFieldException {
            if (numberOfParticipants <= 0) {
                throw new InvalidValueFieldException("");
            }
            this.numberOfParticipants = numberOfParticipants;
            return this;
        }

        public MusicBandBuilder genre(MusicGenre genre) {
            this.genre = genre;
            return this;
        }

        public MusicBandBuilder studio(Studio studio) {
            this.studio = studio;
            return this;
        }


        /**
         *
         * @return готовый объект {@link MusicBand} с инициализированными до этого полями
         */
        public MusicBand build() {
            return new MusicBand(this);
        }
    }
}
