package airmont.core.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class DownloadServerTest {

    @Test
    public void urlParsed() {
        UrlSpy urlSpy = new UrlSpy();
        Server server = DownloadServerFactory.create(urlSpy);
        try {
            server.start();
            String expectedDownloadUrl = "https://some.file.com";
            URL url = new URL("http", "localhost", DownloadServerFactory.PORT, DownloadEndpoint.DOWNLOAD + "?" + DownloadEndpoint.URL + "=" + expectedDownloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            assertEquals(DownloadEndpoint.RESPONSE_CODE_URL_OK, connection.getResponseCode());
            connection.disconnect();
            Thread.sleep(200);
            assertTrue(urlSpy.downloadCalled);
            assertEquals(expectedDownloadUrl, urlSpy.url.toString());
        } catch (Exception e) {
            fail();
        } finally {
            server.stop();
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
