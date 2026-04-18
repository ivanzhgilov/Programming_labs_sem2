package structs;

/**
 * Жанр - одно из полей {@link MusicBand}
 * @see Coordinates
 * @see Studio
 */

public enum MusicGenre {
    ROCK,
    PROGRESSIVE_ROCK,
    RAP,
    MATH_ROCK;

//    public String toString(){
//        return (switch (this) {
//            case ROCK -> "рок";
//            case PROGRESSIVE_ROCK ->  "прогрессивный рок";
//            case RAP -> "рэп";
//            case MATH_ROCK -> "математический рок";
//        });
//    }

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
