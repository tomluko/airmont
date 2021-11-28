package airmont.core.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

class FileServer {

    /**
     * endpoints
     */
    static final String DOWNLOAD = "/download";

    private final int port;
    private final Path file;

    private HttpServer server;

    FileServer(Path file, int port) {
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("File does not exist " + file);
        }
        this.file = file;
        this.port = port;
    }

    void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(DOWNLOAD, new FileHandler(file));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    void stop() {
        server.stop(0);
    }

    private record FileHandler(Path file) implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Content-Type", "text/html");
            try (OutputStream out = httpExchange.getResponseBody()) {
                if (Files.exists(file)) {
                    httpExchange.sendResponseHeaders(200, Files.size(file));
                    out.write(Files.readAllBytes(file));
                } else {
                    System.err.println("File not found: " + file.toAbsolutePath());
                    httpExchange.sendResponseHeaders(404, 0);
                    out.write("404 File not found.".getBytes());
                }
            }
        }
    }
}
