package airmont.core.downloadtarget;

import airmont.core.TempFileTestCase;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DownloadTargetMetaInformationTest extends TempFileTestCase {

    @Test
    public void createMetaFile() throws Exception {
        Path tempFile = createTempFile();
        DownloadTargetMetaInformation expected = new DownloadTargetMetaInformation()
                .setUrl(new URL("http://some.url"))
                .setDestinationFile(tempFile)
                .setMetaInfo("some\nmeta\ninfo");
        expected.createFile();
        Path metaFile = DownloadTargetMetaInformation.getFile(tempFile);
        assertTrue(Files.exists(metaFile));
        DownloadTargetMetaInformation actual = DownloadTargetMetaInformation.read(metaFile);
        assertEquals(expected, actual);
    }

    @Test
    public void overwriteMetaFile() throws Exception {
        Path tempFile = createTempFile();
        DownloadTargetMetaInformation expected = new DownloadTargetMetaInformation()
                .setUrl(new URL("http://some.url"))
                .setDestinationFile(tempFile);
        expected.createFile();
        expected.setMetaInfo("some meta info");
        expected.createFile();
        assertNotEquals(expected, DownloadTargetMetaInformation.read(DownloadTargetMetaInformation.getFile(tempFile)));
    }

    @Test
    public void findAllMetaFiles() throws Exception {
        Set<DownloadTargetMetaInformation> expected = Set.of(new DownloadTargetMetaInformation()
                        .setUrl(new URL("http://some.url"))
                        .setDestinationFile(createTempFile()),
                new DownloadTargetMetaInformation()
                        .setUrl(new URL("http://some.another.url"))
                        .setDestinationFile(createTempFile()));
        expected.forEach(DownloadTargetMetaInformation::createFile);
        Set<DownloadTargetMetaInformation> actual = new HashSet<>(DownloadTargetMetaInformation.findAll(createTempFile().getParent()));
        assertEquals(expected, actual);
    }
}