package airmont.core.downloadtarget;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DownloadTargetMetaInformation {

    private static final String FILE_EXTENSION_META = ".meta";

    private URL url;
    private Path destinationFile;
    private String metaInfo = "";

    public URL getUrl() {
        return url;
    }

    public DownloadTargetMetaInformation setUrl(URL url) {
        this.url = url;
        return this;
    }

    public Path getDestinationFile() {
        return destinationFile;
    }

    public DownloadTargetMetaInformation setDestinationFile(Path destinationFile) {
        this.destinationFile = destinationFile;
        return this;
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public DownloadTargetMetaInformation setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
        return this;
    }

    public static List<DownloadTargetMetaInformation> findAll(Path dir) {
        try {
            return Files.walk(dir, 1)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION_META))
                    .map(DownloadTargetMetaInformation::read)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DownloadTargetMetaInformation read(Path file) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(file)))) {
            return read(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DownloadTargetMetaInformation read(BufferedReader in) {
        try {
            DownloadTargetMetaInformation parameters = new DownloadTargetMetaInformation()
                    .setUrl(new URL(in.readLine()))
                    .setDestinationFile(Paths.get(in.readLine()));
            String line;
            StringBuilder metaInfo = new StringBuilder();
            while ((line = in.readLine()) != null) {
                metaInfo.append(line).append("\n");
            }
            parameters.setMetaInfo(metaInfo.toString().trim());
            return parameters;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createFile() {
        Path metaInformationFile = getFile();
        if (Files.exists(metaInformationFile)) {
            return;
        }
        try {
            Files.createFile(metaInformationFile);
            try (PrintStream out = new PrintStream(createBufferedOutputStream(metaInformationFile))) {
                write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile() {
        Path metaInformationFile = getFile();
        try {
            Files.deleteIfExists(metaInformationFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getFile() {
        return getFile(destinationFile);
    }

    public static Path getFile(Path destinationFile) {
        return destinationFile.resolveSibling(destinationFile.getFileName() + FILE_EXTENSION_META);
    }

    private static BufferedOutputStream createBufferedOutputStream(Path destinationFile) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(destinationFile));
    }

    private void write(PrintStream out) {
        if (url == null) {
            throw new RuntimeException("missing url");
        }
        if (destinationFile == null) {
            throw new RuntimeException("missing destination file");
        }
        out.println(url);
        out.println(destinationFile);
        out.println(metaInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadTargetMetaInformation that = (DownloadTargetMetaInformation) o;
        return Objects.equals(url, that.url) && Objects.equals(destinationFile, that.destinationFile) && Objects.equals(metaInfo, that.metaInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, destinationFile, metaInfo);
    }
}
