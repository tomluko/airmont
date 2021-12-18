package airmont.core.connection;

import java.io.IOException;
import java.net.URL;

public class Connections {

    public static AutoClosableHttpURLConnection create(URL url) throws IOException {
        return new AutoClosableHttpURLConnection(url);
    }
}
