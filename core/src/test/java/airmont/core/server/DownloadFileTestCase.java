package airmont.core.server;

import airmont.core.TempFileTestCase;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@ExtendWith(DownloadFileExtension.class)
@DownloadFile(port = 32581, file = "file.png")
public class DownloadFileTestCase extends TempFileTestCase {

    protected static URL getWebUrl() {
        try {
            return new URL("http", "localhost", 32581, FileUploadEndpoint.ENDPOINT);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Path getActualFile() {
        try {
            return Paths.get(Objects.requireNonNull(DownloadFileTestCase.class.getClassLoader().getResource("file.png")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
