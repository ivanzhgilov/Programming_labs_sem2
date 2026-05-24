package structs;

import exceptions.InvalidFieldValueException;
import exceptions.InvalidValueFieldException;
import utils.StructureValidator;

import java.io.Serial;
import java.io.Serializable;

/**
 * Координаты - одно из полей {@link MusicBand}.
 */
public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int x;
    private final Double y;

    private Coordinates(CoordinatesBuilder builder) {
        this.x = builder.x;
        this.y = builder.y;
    }

    public static CoordinatesBuilder builder() {
        return new CoordinatesBuilder();
    }

    public int getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "{" + "x=" + x + ", y=" + y + '}';
    }

    public static class CoordinatesBuilder {
        private static final int UNSET_X = 618;
        private static final Double UNSET_Y = -371D;

        private int x = UNSET_X;
        private Double y = UNSET_Y;

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

        public CoordinatesBuilder y(Double y) throws InvalidValueFieldException {
            if (y <= -370) {
                throw new InvalidValueFieldException("");
            }
            this.y = y;
            return this;
        }

        public Coordinates build() throws InvalidValueFieldException {
            Coordinates coords = new Coordinates(this);
            try {
                StructureValidator.validateCoordinates(coords);
                return coords;
            } catch (InvalidFieldValueException e) {
                throw new InvalidValueFieldException(e.getMessage());
            }
        }
    }
}
