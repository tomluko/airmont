package airmont.core.server;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;

public class Server {

    private final int port;

    private HttpServer server;

    private final Collection<Endpoint> endpoints = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void add(Endpoint endpoint) {
        endpoints.add(endpoint);
    }

    public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        endpoints.forEach(endpoint -> server.createContext(endpoint.getEndpoint(), endpoint));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

}
