package structs;


import exceptions.InitializationException;
import exceptions.InitializationExceptionType;
import exceptions.InvalidValueFieldException;

/**
 * Координаты - одно из полей {@link MusicBand}
 *
 * @see MusicGenre
 * @see Studio
 */

public class Coordinates {
    private final int x; //Максимальное значение поля: 617
    private final Double y; //Значение поля должно быть больше -370, Поле не может быть null

    private Coordinates(CoordinatesBuilder builder) {
        try {
            if (builder.x == 618 || builder.y == -371D) {
                throw new InitializationException(InitializationExceptionType.NULL);
            }
        } catch (InitializationException e) {
            System.out.println(e.getMessage());
        }
        this.x = builder.x;
        this.y = builder.y;
    }

    @Override
    public String toString() {
        return "{" + "x=" + x + ", y=" + y + '}';
    }

    /**
     * Создает билдер для инициализации Coordinates
     */
    public static CoordinatesBuilder builder() {
        return new CoordinatesBuilder();
    }

    public int getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    /**
     * Билдер класса Coordinates
     */

    public static class CoordinatesBuilder {
        private int x = 618;
        private Double y = -371D;

        private CoordinatesBuilder() {
            super();
        }

        public CoordinatesBuilder x(int x) throws InvalidValueFieldException {
            if (x > 670) {
                throw new InvalidValueFieldException("");
            }
            this.x = x;
            return this;
        }

        /**
         * Инициализация координаты y
         *
         * @param y координата
         */
        public CoordinatesBuilder y(Double y) throws InvalidValueFieldException {
            if (y <= -370) {
                throw new InvalidValueFieldException("");
            }
            this.y = y;
            return this;
        }

        /**
         *
         * @return Готовый объект Coordinates
         */
        public Coordinates build() {
            return new Coordinates(this);
        }
    }
}
