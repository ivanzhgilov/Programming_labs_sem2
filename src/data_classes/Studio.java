package data_classes;

public class Studio {
    private String address; //Поле не может быть null

    private Studio(StudioBuilder builder) {
        this.address = builder.address;
    }

    public static class StudioBuilder {
        private String address;

        public StudioBuilder () {
            super();
        }

        public StudioBuilder address (String name) {
            this.address = name;
            return this;
        }
    }
}
