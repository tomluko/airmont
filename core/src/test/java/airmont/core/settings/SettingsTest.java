package airmont.core.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void getDefaultWindowsUserDownloadDir() {
        Settings settings = new Settings(Paths.get("some", "empty", "path"));
        Path downloadDir = settings.getDownloadDir();
        assertEquals(System.getProperty("user.home") + File.separator + "Downloads", downloadDir.toString());
    }

    @Test
    public void getDownloadDirFromProperties() throws IOException {
        Path tempDir = getTempDir();
        Path propertiesFile = getPropertiesFile(tempDir);

        Properties properties = new Properties();
        String expectedDownloadDir = Paths.get("some", "dir").toString();
        properties.put("PROPERTY_DOWNLOAD_DIR", expectedDownloadDir);
        properties.store(Files.newOutputStream(propertiesFile), "");

        Settings settings = new Settings(tempDir);
        Path downloadDir = settings.getDownloadDir();
        assertEquals(expectedDownloadDir, downloadDir.toString());
    }

    @Test
    public void changeDownloadDirInProperties() throws IOException {
        Path tempDir = getTempDir();
        Path propertiesFile = getPropertiesFile(tempDir);

        Properties properties = new Properties();
        properties.put("PROPERTY_DOWNLOAD_DIR", Paths.get("some", "dir").toString());
        properties.store(Files.newOutputStream(propertiesFile), "");

        Settings settings = new Settings(tempDir);
        Path expectedDownloadDir = Paths.get("some", "other", "dir");
        settings.setDownloadDir(expectedDownloadDir);
        settings.save();

        settings = new Settings(tempDir);
        Path downloadDir = settings.getDownloadDir();
        assertEquals(expectedDownloadDir.toString(), downloadDir.toString());
    }

    private Path getTempDir() throws IOException {
        Path temp = Files.createTempFile("tmp", "");
        temp.toFile().deleteOnExit();
        return temp.getParent();
    }

    private Path getPropertiesFile(Path dir) {
        Path propertiesFile = dir.resolve("airmont.properties");
        propertiesFile.toFile().deleteOnExit();
        return propertiesFile;
    }
}