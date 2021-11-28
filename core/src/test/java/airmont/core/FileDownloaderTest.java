package airmont.core;

import airmont.core.server.DownloadFileTestCase;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FileDownloaderTest extends DownloadFileTestCase {

    @Test
    public void downloadFileAtOnce() throws Exception {
        URL url = getWebUrl();
        Path tmp = createTmpFile();
        FileDownloader.download(url, tmp);
        assertArrayEquals(MD5(getActualFile()), MD5(tmp));
    }

    private Path createTmpFile() throws IOException {
        Path tmp = Files.createTempFile("tmp", "");
        tmp.toFile().deleteOnExit();
        return tmp;
    }

    private static byte[] MD5(Path path) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        try (BufferedInputStream is = new BufferedInputStream(new DigestInputStream(Files.newInputStream(path), md5))) {
            is.readAllBytes();
        }
        return md5.digest();
    }
}