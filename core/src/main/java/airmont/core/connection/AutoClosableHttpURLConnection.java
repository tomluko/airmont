package airmont.core.connection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoClosableHttpURLConnection implements AutoCloseable {

    public static final int EXPECTED_RESPONSE_CODE_RESUME_DOWNLOAD = 206;
    public static final int EXPECTED_RESPONSE_CODE_START_DOWNLOAD = 200;

    private final HttpURLConnection connection;

    AutoClosableHttpURLConnection(URL url) throws IOException {
        connection = (HttpURLConnection) url.openConnection();
    }

    public void verifyResponseCode(int expectedResponseCode) throws IOException {
        int responseCode = getResponseCode();
        if (responseCode != expectedResponseCode) {
            throw new UnexpectedResponseException(responseCode, expectedResponseCode);
        }
    }

    public int getResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    public UrlConnectionHeader getHeaderFields() {
        return new UrlConnectionHeader(connection.getHeaderFields());
    }

    public void addResumeRequestProperty(long offset) {
        connection.setRequestProperty("Range", "bytes=" + offset + "-");
    }

    public BufferedInputStream createBufferedInputStream() throws IOException {
        return new BufferedInputStream(connection.getInputStream());
    }

    @Override
    public void close() {
        if (connection != null) {
            connection.disconnect();
        }
    }

}
