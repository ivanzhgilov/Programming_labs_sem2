package structs;

import exceptions.InvalidFieldValueException;
import exceptions.InvalidValueFieldException;
import utils.StructureValidator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Студия - одно из полей {@link MusicBand}.
 */
public class Studio implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String address;

    private Studio(StudioBuilder builder) {
        this.address = builder.address;
    }

    public String getAddress() {
        return address;
    }

    public static StudioBuilder builder() {
        return new StudioBuilder();
    }

    public static class StudioBuilder {
        private String address;

        public StudioBuilder() {
            super();
        }

        public StudioBuilder address(String address) throws InvalidValueFieldException {
            if (address.isEmpty()) {
                throw new InvalidValueFieldException("Адрес не может быть пустым");
            }
            this.address = address;
            return this;
        }

        public Studio build() throws InvalidValueFieldException {
            Studio studio = new Studio(this);
            try {
                StructureValidator.validateStudio(studio);
                return studio;
            } catch (InvalidFieldValueException e) {
                throw new InvalidValueFieldException(e.getMessage());
            }
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
