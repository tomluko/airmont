package airmont.core.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DownloadEndpoint extends Endpoint {

    static final int RESPONSE_CODE_NO_URL = 404;
    static final int RESPONSE_CODE_URL_OK = 200;

    public static final String ENDPOINT = "/download";
    public static final String URL = "url";

    private final DownloadHandler downloadHandler;

    public DownloadEndpoint(DownloadHandler downloadHandler) {
        super(ENDPOINT);
        this.downloadHandler = downloadHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = getParameters(exchange.getRequestURI().getQuery());
        String url = params.get(URL);
        if (url == null) {
            exchange.sendResponseHeaders(RESPONSE_CODE_NO_URL, 0);
            return;
        }
        exchange.sendResponseHeaders(RESPONSE_CODE_URL_OK, 0);
        downloadHandler.download(new URL(url));
    }

    private static Map<String, String> getParameters(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            result.put(entry[0], entry.length > 1 ? entry[1] : "");
        }
        return result;
    }

}
