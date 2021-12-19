package airmont.core;

import airmont.core.downloadtarget.DownloadTargetMetaInformation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

public class TempFileTestCase {

    @BeforeEach
    public void beforeEach() throws IOException {
        clearAllMetaFilesFromTempDir();
    }

    @AfterEach
    public void afterEach() throws IOException {
        clearAllMetaFilesFromTempDir();
    }

    protected static Path getTempDir() throws IOException {
        return createTempFile().getParent();
    }

    protected static Path createTempFile() throws IOException {
        Path tempFile = Files.createTempFile("temp", "");
        tempFile.toFile().deleteOnExit();
        return tempFile;
    }

    private static void clearAllMetaFilesFromTempDir() throws IOException {
        DownloadTargetMetaInformation.findAll(createTempFile().getParent()).stream()
                .map(DownloadTargetMetaInformation::getFile)
                .forEach(file -> {
                    try {
                        Files.deleteIfExists(file);
                    } catch (IOException e) {
                        fail(e);
                    }
                });
    }
}
