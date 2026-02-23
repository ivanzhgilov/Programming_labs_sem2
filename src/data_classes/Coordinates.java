package data_classes;

public class Coordinates {
    private int x; //Максимальное значение поля: 617
    private Double y; //Значение поля должно быть больше -370, Поле не может быть null

    private Coordinates(CoordinatesBuilder builder) {
        this.x = builder.x;
        this.y = builder.y;
    }

    public static class CoordinatesBuilder {
        private int x;
        private Double y;

        private CoordinatesBuilder() {
            super();
        }

        public CoordinatesBuilder x(int x) {
            this.x = x;
            return this;
        }

        public CoordinatesBuilder y(Double y) {
            this.y = y;
            return this;
        }
    }
}
