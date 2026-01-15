package webserver.template;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HtmlComponentList implements Renderable {
    private final List<HtmlComponent> children;

    public HtmlComponentList(List<HtmlComponent> children) {
        this.children = children;
    }

    @Override
    public String render() {
        return IntStream.range(0, children.size())
            .mapToObj((i) -> children.get(i).renderByIndex(i))
            .collect(Collectors.joining(""));
    }

}
