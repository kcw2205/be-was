package webserver.resources;

import java.io.File;

public enum ResourcePath {
    EXTERNAL_STORAGE(System.getProperty("user.dir") + File.separator + "storage"),
    INTERNAL_STATIC("static"),
    ;

    private final String path;

    ResourcePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
