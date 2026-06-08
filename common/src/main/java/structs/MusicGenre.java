package structs;

import java.io.Serializable;

/**
 * Жанр - одно из полей {@link MusicBand}
 * @see Coordinates
 * @see Studio
 */

public enum MusicGenre implements Serializable {
    ROCK,
    PROGRESSIVE_ROCK,
    RAP,
    MATH_ROCK;

    private static final long serialVersionUID = 1L;

    public static String getAllGenre(){
        StringBuilder genres = new StringBuilder();
        for (MusicGenre g : MusicGenre.values()){
            genres.append(g.name()).append(", ");
        }
        genres.deleteCharAt(genres.length()-1);
        genres.deleteCharAt(genres.length()-1);
        return genres.toString();
    }
}
