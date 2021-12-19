package airmont.core.downloadtarget;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DownloadTarget {

    private final DownloadTargetMetaInformation parameters;

    public static List<DownloadTarget> findAll(Path dir) {
        return DownloadTargetMetaInformation.findAll(dir).stream()
                .map(DownloadTarget::new)
                .collect(Collectors.toList());
    }

    public DownloadTarget(DownloadTargetMetaInformation parameters) {
        this.parameters = parameters;
        createFilesIfNotExist();
    }

    private void createFilesIfNotExist() {
        Path destinationFile = parameters.getDestinationFile();
        if (Files.exists(destinationFile)) {
            return;
        }
        Path destinationDir = destinationFile.getParent();
        if (!Files.isWritable(destinationDir)) {
            return;
        }
        createFile(destinationFile);
        parameters.createFile();
    }

    private static void createFile(Path file) {
        try {
            Files.createFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedOutputStream createBufferedOutputStream() throws IOException {
        return createBufferedOutputStream(parameters.getDestinationFile());
    }

    public static BufferedOutputStream createBufferedOutputStream(Path destinationFile) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(destinationFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }

    public long getFileSize() throws IOException {
        return getFileSize(parameters.getDestinationFile());
    }

    public static long getFileSize(Path file) throws IOException {
        return Files.exists(file) && !Files.isDirectory(file) ?
                Files.size(file) :
                0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadTarget that = (DownloadTarget) o;
        return Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}
