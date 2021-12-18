package airmont.core.server;

import airmont.core.connection.AutoClosableHttpURLConnection;
import airmont.core.connection.Connections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class DownloadServerTest {

    @Test
    public void urlParsed() throws MalformedURLException {
        UrlSpy urlSpy = new UrlSpy();
        String expectedDownloadUrl = "https://some.file.com";
        int port = 32582;
        URL url = new URL("http", "localhost", port, DownloadEndpoint.ENDPOINT + "?" + DownloadEndpoint.URL + "=" + expectedDownloadUrl);
        //noinspection unused
        try (Server server = DownloadServers.create(urlSpy, port).start();
             AutoClosableHttpURLConnection connection = Connections.create(url)) {
            assertEquals(DownloadEndpoint.RESPONSE_CODE_URL_OK, connection.getResponseCode());
            Thread.sleep(200);
            assertTrue(urlSpy.downloadCalled);
            assertEquals(expectedDownloadUrl, urlSpy.url.toString());
        } catch (Exception e) {
            fail();
        }
    }

    private static class UrlSpy implements DownloadHandler {

        boolean downloadCalled = false;
        URL url;

        @Override
        public void download(URL url) {
            Assertions.assertNotNull(url);
            this.url = url;
            downloadCalled = true;
        }
    }

}
