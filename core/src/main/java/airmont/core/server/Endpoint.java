package airmont.core.server;

import com.sun.net.httpserver.HttpHandler;

public abstract class Endpoint implements HttpHandler {

    private final String endpoint;

    public Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
