package airmont.core.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

class FileUploadEndpoint extends Endpoint {

    static final String ENDPOINT = "/upload";

    private final Path file;

    public FileUploadEndpoint(Path file) {
        super(ENDPOINT);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("File does not exist " + file);
        }
        this.file = file;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "text/html");
        Headers requestHeaders = httpExchange.getRequestHeaders();
        int offset = getFileOffset(requestHeaders);
        try (OutputStream out = httpExchange.getResponseBody()) {
            if (Files.exists(file)) {
                serveFile(httpExchange, offset, out);
            } else {
                System.err.println("File not found: " + file.toAbsolutePath());
                httpExchange.sendResponseHeaders(404, 0);
                out.write("404 File not found.".getBytes());
            }
        }
    }

    private void serveFile(HttpExchange httpExchange, int offset, OutputStream out) throws IOException {
        if (offset > 0) {
            byte[] allBytes = Files.readAllBytes(file);
            byte[] bytes = Arrays.copyOfRange(allBytes, offset, allBytes.length);
            httpExchange.sendResponseHeaders(206, bytes.length);
            out.write(bytes);
        } else {
            httpExchange.sendResponseHeaders(200, Files.size(file));
            out.write(Files.readAllBytes(file));
        }
    }

    private static int getFileOffset(Headers requestHeaders) {
        List<String> ranges = requestHeaders.get("Range");
        if (ranges != null && ranges.size() == 1) {
            String range = ranges.get(0);
            return Integer.parseInt(range.substring("bytes=".length(), range.indexOf("-")));
        }
        return 0;
    }
}
