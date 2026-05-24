package structs;

import exceptions.InvalidFieldValueException;
import exceptions.InvalidValueFieldException;
import utils.StructureValidator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Основной класс данных коллекции музыкальных групп.
 *
 * Правило по id:
 * - id > 0 для объектов, которые уже существуют в коллекции (сервер/файл)
 * - id == 0 допустим только для create-запросов клиента (сервер сгенерирует id)
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final Coordinates coordinates;
    private final Date creationDate;
    private final Integer numberOfParticipants;
    private final MusicGenre genre;
    private final Studio studio;

    private MusicBand(MusicBandBuilder builder) {
        this.id = builder.id;
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
        builder.append("studio: ").append(studio).append("\n");
        return builder.toString();
    }

    public static MusicBandBuilder builder() {
        return new MusicBandBuilder();
    }

    public static class MusicBandBuilder {
        private int id; // default 0: create request
        private String name;
        private Coordinates coordinates;
        private Date creationDate;
        private Integer numberOfParticipants;
        private MusicGenre genre;
        private Studio studio;

        public MusicBandBuilder() {
            super();
        }

        public MusicBandBuilder id(int id) {
            this.id = id;
            return this;
        }

        public int getId() {
            return id;
        }

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

        public MusicBand build() throws InvalidValueFieldException {
            if (id < 0) {
                throw new InvalidValueFieldException("Поле id не может быть отрицательным");
            }
            MusicBand band = new MusicBand(this);
            try {
                StructureValidator.validateMusicBand(band);
                return band;
            } catch (InvalidFieldValueException e) {
                throw new InvalidValueFieldException(e.getMessage());
            }
        }
    }
}
