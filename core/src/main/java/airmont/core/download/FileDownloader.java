package airmont.core.download;

import airmont.core.connection.AutoClosableHttpURLConnection;
import airmont.core.connection.Connections;
import airmont.core.connection.UrlConnectionHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileDownloader {

    private static final int DATA_BUFFER_SIZE = 4096;
    private final byte[] dataBuffer;

    private volatile boolean stop = false;

    public FileDownloader() {
        dataBuffer = new byte[DATA_BUFFER_SIZE];
    }

    /**
     * @param url         http, https url
     * @param destination where to save bytes or dir to create new file
     * @throws IOException if fails to start the download
     */
    public Path download(URL url, Path destination) throws IOException {
        return download(url, destination, new EmptyFileDownloadCallback());
    }

    /**
     * @param url         http, https url
     * @param destination where to save bytes or dir to create new file
     * @throws IOException if fails to start the download
     */
    public Path download(URL url, Path destination, FileDownloadCallback callback) throws IOException {
        callback.before(url, destination);
        AutoClosableHttpURLConnection connection = Connections.create(url);
        Path destinationFile;
        long fileSize = getFileSize(destination);
        if (fileSize > 0) {
            connection.addResumeRequestProperty(fileSize);
            connection.verifyResponseCode(AutoClosableHttpURLConnection.EXPECTED_RESPONSE_CODE_RESUME_DOWNLOAD);
            callback.resume(fileSize, connection.getHeaderFields());
            destinationFile = destination;
        } else {
            connection.verifyResponseCode(AutoClosableHttpURLConnection.EXPECTED_RESPONSE_CODE_START_DOWNLOAD);
            UrlConnectionHeader header = connection.getHeaderFields();
            callback.start(header);
            if (Files.isDirectory(destination)) {
                destinationFile = Paths.get(destination.toAbsolutePath().toString(), getFileName(url, header).toString());
                if (!Files.exists(destinationFile)) {
                    Files.createFile(destinationFile);
                }
            } else {
                destinationFile = destination;
            }
        }
        try (BufferedInputStream in = connection.createBufferedInputStream();
             BufferedOutputStream out = createBufferedOutputStream(destinationFile)) {
            copy(callback, in, out);
        } catch (Exception e) {
            callback.exception(e);
        } finally {
            callback.finish(stop);
        }
        return destinationFile;
    }

    private Path getFileName(URL url, UrlConnectionHeader header) {
        String fileName;
        fileName = header.getFileName();
        if (fileName == null) {
            fileName = getFileNameFromUrl(url);
        }
        if (fileName == null) {
            fileName = createFileName();
        }
        return Paths.get(fileName);
    }

    private String getFileNameFromUrl(URL url) {
        String file = url.getFile();
        int lastSlash = file.lastIndexOf("/");
        if (lastSlash > -1) {
            return file.substring(lastSlash);
        }
        return null;
    }

    private String createFileName() {
        return "unknownFile" + System.currentTimeMillis();
    }

    private static long getFileSize(Path file) throws IOException {
        return Files.exists(file) && !Files.isDirectory(file) ?
                Files.size(file) :
                0;
    }

    private void copy(FileDownloadCallback callback, BufferedInputStream in, BufferedOutputStream out) throws IOException {
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, DATA_BUFFER_SIZE)) != -1 && !stop) {
            out.write(dataBuffer, 0, bytesRead);
            callback.read(bytesRead);
        }
    }

    public void stop() {
        stop = true;
    }

    private static BufferedOutputStream createBufferedOutputStream(Path destinationFile) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(destinationFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
