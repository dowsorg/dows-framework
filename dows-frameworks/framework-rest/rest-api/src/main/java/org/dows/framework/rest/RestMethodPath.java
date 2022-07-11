package org.dows.framework.rest;

public class RestMethodPath {
    /**
     * request method. such as GET, POST, PUT etc.
     */
    private final String method;

    /**
     * request path
     */
    private final String path;

    public RestMethodPath(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
