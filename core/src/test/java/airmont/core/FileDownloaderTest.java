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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileDownloaderTest extends DownloadFileTestCase {

    @Test
    public void downloadFileAtOnce() throws Exception {
        URL url = getWebUrl();
        Path tmp = createTmpFile();
        SpyCallback spyCallback = new SpyCallback();
        new FileDownloader().download(url, tmp, spyCallback);
        assertEquals("134444444444444444444444444444444444444444444444444444444444444444444444444444444446", spyCallback.methodsCalled);
        assertArrayEquals(MD5(getActualFile()), MD5(tmp));
    }

    @Test
    public void resumeDownload() throws Exception {
        URL url = getWebUrl();
        Path tmp = createTmpFile();
        FileDownloader fileDownloader = new FileDownloader();
        SpyCallback spyCallback = new SpyCallback() {
            @Override
            public void read(int bytesRead) {
                super.read(bytesRead);
                fileDownloader.stop();
            }
        };
        fileDownloader.download(url, tmp, spyCallback);
        assertEquals("1346", spyCallback.methodsCalled);
        spyCallback = new SpyCallback();
        new FileDownloader().download(url, tmp, spyCallback);
        assertEquals("13444444444444444444444444444444444444444444444444444444444444444444444444444444446", spyCallback.methodsCalled);
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

    private static class SpyCallback implements FileDownloadCallback {

        private String methodsCalled = "";

        @Override
        public void before(URL url, Path destinationFile) {
            methodsCalled += "1";
        }

        @Override
        public void start() {
            methodsCalled += "2";
        }

        @Override
        public void resume() {
            methodsCalled += "3";
        }

        @Override
        public void read(int bytesRead) {
            methodsCalled += "4";
        }

        @Override
        public void exception(Exception e) {
            methodsCalled += "5";
            e.printStackTrace();
        }

        @Override
        public void finish(boolean stop) {
            methodsCalled += "6";
        }
    }

}