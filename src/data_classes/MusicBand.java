package data_classes;

import java.util.Date;

public class MusicBand {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer numberOfParticipants; //Поле не может быть null, Значение поля должно быть больше 0
    private MusicGenre genre; //Поле может быть null
    private Studio studio; //Поле не может быть null

    private MusicBand(MusicBandBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.coordinates = builder.coordinates;
        this.creationDate = builder.creationDate;
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

        public MusicBandBuilder id(int id) {
            this.id = id;
            return this;
        }

        public MusicBandBuilder name(String name) {
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

        public MusicBandBuilder numberOfParticipants(Integer numberOfParticipants) {
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
        public MusicBand build() {
            return new  MusicBand(this);
        }
    }
}
