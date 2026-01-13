package webserver.template;

public class RawString implements Renderable {
    private final String value;

    public RawString(String value) {
        this.value = value;
    }

    @Override
    public String render() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
