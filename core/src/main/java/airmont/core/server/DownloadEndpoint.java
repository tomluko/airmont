package airmont.core.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class DownloadEndpoint extends Endpoint {

    static final String DOWNLOAD = "/download";
    static final String URL = "url";

    private final DownloadHandler downloadHandler;

    public DownloadEndpoint(DownloadHandler downloadHandler) {
        super(DOWNLOAD);
        this.downloadHandler = downloadHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = getParameters(exchange.getRequestURI().getQuery());
        String url = params.get(URL);
        if (url == null) {
            exchange.sendResponseHeaders(404, 0);
            return;
        }
        exchange.sendResponseHeaders(200, 0);
        downloadHandler.download(new URL(url));
    }

    private static Map<String, String> getParameters(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

}
