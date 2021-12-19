package airmont.core.downloadtarget;

import airmont.core.TempFileTestCase;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DownloadTargetTest extends TempFileTestCase {

    @Test
    public void createMetaFileIfNotExist() throws IOException {
        Path tempFile = createTempFile();
        Files.delete(tempFile);
        DownloadTargetMetaInformation parameters = new DownloadTargetMetaInformation()
                .setUrl(new URL("http://some.url"))
                .setDestinationFile(tempFile);
        DownloadTarget expected = new DownloadTarget(parameters);
        assertEquals(expected, new DownloadTarget(parameters));
        Set<DownloadTarget> actual = new HashSet<>(DownloadTarget.findAll(tempFile.getParent()));
        assertEquals(Set.of(expected), actual);
        actual = new HashSet<>(DownloadTarget.findAll(tempFile.getParent()));
        assertEquals(Set.of(expected), actual);
    }

    @Test
    public void appendBytes() throws IOException {
        Path tempFile = createTempFile();
        Files.delete(tempFile);
        DownloadTargetMetaInformation parameters = new DownloadTargetMetaInformation()
                .setUrl(new URL("http://some.url"))
                .setDestinationFile(tempFile);
        DownloadTarget target = new DownloadTarget(parameters);
        try (BufferedOutputStream out = target.createBufferedOutputStream()) {
            out.write(new byte[]{1});
        }
        target = new DownloadTarget(parameters);
        try (BufferedOutputStream out = target.createBufferedOutputStream()) {
            out.write(new byte[]{2});
        }
        byte[] actual = Files.readAllBytes(parameters.getDestinationFile());
        assertArrayEquals(new byte[]{1, 2}, actual);
    }

}