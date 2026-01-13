package webserver.template;

import java.util.List;
import java.util.stream.Collectors;

public class RenderableList implements Renderable {
    private final List<Renderable> children;

    public RenderableList(List<Renderable> children) {
        this.children = children;
    }

    @Override
    public String render() {
        return children.stream()
            .map(Renderable::render)
            .collect(Collectors.joining(""));
    }

}
