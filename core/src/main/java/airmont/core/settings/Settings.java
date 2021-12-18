package airmont.core.settings;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Settings {

    private static final String PROPERTIES_FILE_NAME = "airmont.properties";

    private static final String PROPERTY_DOWNLOAD_DIR = "PROPERTY_DOWNLOAD_DIR";
    private static final String PROPERTY_SIMULTANEOUS_DOWNLOADS_COUNT = "PROPERTY_SIMULTANEOUS_DOWNLOADS_COUNT";
    private static final String PROPERTY_SERVER_PORT = "PROPERTY_SERVER_PORT";

    private final Properties properties;
    private final Path propertiesDir;

    public Settings() {
        this(Paths.get("."));
    }

    public Settings(Path propertiesDir) {
        this.propertiesDir = propertiesDir;
        try {
            properties = loadOrDefaultProperties(getPropertiesFile(propertiesDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getPropertiesFile(Path propertiesDir) {
        return propertiesDir.resolve(PROPERTIES_FILE_NAME);
    }

    private static Properties loadOrDefaultProperties(Path propertiesFile) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = Files.exists(propertiesFile) ?
                Files.newInputStream(propertiesFile) :
                Settings.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
        properties.load(inputStream);
        return properties;
    }

    public void save() {
        try {
            saveProperties(getPropertiesFile(propertiesDir), properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveProperties(Path propertiesFile, Properties properties) throws IOException {
        if (Files.exists(propertiesFile)) {
            if (!Files.isWritable(propertiesFile)) {
                return;
            }
        } else {
            Path propertiesDir = propertiesFile.getParent();
            if (!Files.isWritable(propertiesDir)) {
                return;
            }
        }
        properties.store(new BufferedOutputStream(Files.newOutputStream(propertiesFile)), "");
    }

    public Path getDownloadDir() {
        Path downloadDirPath;
        String downloadDir = properties.getProperty(PROPERTY_DOWNLOAD_DIR);
        if (downloadDir.isBlank()) {
            String userHome = System.getProperty("user.home");
            Path userHomePath = Paths.get(userHome);
            downloadDirPath = userHomePath.resolve("Downloads");
        } else {
            downloadDirPath = Paths.get(downloadDir);
        }
        return downloadDirPath;
    }

    public void setDownloadDir(Path downloadDir) {
        properties.put(PROPERTY_DOWNLOAD_DIR, downloadDir.toString());
    }

    public int getSimultaneousDownloadsCount() {
        String count = properties.getProperty(PROPERTY_SIMULTANEOUS_DOWNLOADS_COUNT);
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return Runtime.getRuntime().availableProcessors();
        }
    }

    public void setSimultaneousDownloadsCount(int count) {
        properties.put(PROPERTY_SIMULTANEOUS_DOWNLOADS_COUNT, count);
    }

    public int getServerPort() {
        try {
            return Integer.parseInt(properties.getProperty(PROPERTY_SERVER_PORT));
        } catch (NumberFormatException e) {
            return 32582;
        }
    }

    public void setServerPort(int port) {
        properties.put(PROPERTY_SERVER_PORT, Integer.toString(port));
    }
}
