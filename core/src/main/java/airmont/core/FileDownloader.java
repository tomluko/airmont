package airmont.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileDownloader {

    private static final int DATA_BUFFER_SIZE = 4096;
    private final byte[] dataBuffer;

    private volatile boolean stop = false;

    public FileDownloader() {
        dataBuffer = new byte[DATA_BUFFER_SIZE];
    }

    /**
     * @throws IOException if fails to start the download
     */
    public void download(URL url, Path destinationFile) throws IOException {
        download(url, destinationFile, new EmptyFileDownloadCallback());
    }

    /**
     * @throws IOException if fails to start the download
     */
    public void download(URL url, Path destinationFile, FileDownloadCallback callback) throws IOException {
        callback.before(url, destinationFile);
        URLConnection connection = url.openConnection();
        if (Files.exists(destinationFile)) {
            addResumeRequestProperty(destinationFile, connection);
            callback.resume(Files.size(destinationFile), connection.getHeaderFields());
        } else {
            callback.start(connection.getHeaderFields());
        }
        try (BufferedInputStream in = createBufferedInputStream(connection);
             BufferedOutputStream out = createBufferedOutputStream(destinationFile)) {
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, DATA_BUFFER_SIZE)) != -1 && !stop) {
                out.write(dataBuffer, 0, bytesRead);
                callback.read(bytesRead);
            }
        } catch (Exception e) {
            callback.exception(e);
        } finally {
            callback.finish(stop);
        }
    }

    public void stop() {
        stop = true;
    }

    private static void addResumeRequestProperty(Path destinationFile, URLConnection connection) throws IOException {
        connection.setRequestProperty("Range", "bytes=" + Files.size(destinationFile) + "-");
    }

    private static BufferedInputStream createBufferedInputStream(URLConnection connection) throws IOException {
        return new BufferedInputStream(connection.getInputStream());
    }

    private static BufferedOutputStream createBufferedOutputStream(Path destinationFile) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(destinationFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
