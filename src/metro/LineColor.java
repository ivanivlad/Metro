package metro;

public enum LineColor {
    RED("Красная"), BLUE("Синяя");

    private final String name;

    LineColor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
