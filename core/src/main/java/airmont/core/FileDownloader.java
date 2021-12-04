package airmont.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileDownloader {

    private static final int EXPECTED_RESUME_DOWNLOAD_RESPONSE_CODE = 206;
    private static final int EXPECTED_START_DOWNLOAD_RESPONSE_CODE = 200;

    private static final int DATA_BUFFER_SIZE = 4096;
    private final byte[] dataBuffer;

    private volatile boolean stop = false;

    public FileDownloader() {
        dataBuffer = new byte[DATA_BUFFER_SIZE];
    }

    /**
     * @param url http, https url
     * @throws IOException if fails to start the download
     */
    public void download(URL url, Path destinationFile) throws IOException {
        download(url, destinationFile, new EmptyFileDownloadCallback());
    }

    /**
     * @param url http, https url
     * @throws IOException if fails to start the download
     */
    public void download(URL url, Path destinationFile, FileDownloadCallback callback) throws IOException {
        callback.before(url, destinationFile);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        long fileSize = getFileSize(destinationFile);
        if (fileSize > 0) {
            addResumeRequestProperty(connection, fileSize);
            verifyResponseCode(connection, EXPECTED_RESUME_DOWNLOAD_RESPONSE_CODE);
            callback.resume(fileSize, connection.getHeaderFields());
        } else {
            verifyResponseCode(connection, EXPECTED_START_DOWNLOAD_RESPONSE_CODE);
            callback.start(connection.getHeaderFields());
        }
        try (BufferedInputStream in = createBufferedInputStream(connection);
             BufferedOutputStream out = createBufferedOutputStream(destinationFile)) {
            copy(callback, in, out);
        } catch (Exception e) {
            callback.exception(e);
        } finally {
            callback.finish(stop);
        }
    }

    private static long getFileSize(Path file) throws IOException {
        return Files.exists(file) ?
                Files.size(file) :
                0;
    }

    private static void verifyResponseCode(HttpURLConnection connection, int expectedResponseCode) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != expectedResponseCode) {
            throw new UnexpectedResponseException(responseCode, expectedResponseCode);
        }
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

    private static void addResumeRequestProperty(URLConnection connection, long offset) {
        connection.setRequestProperty("Range", "bytes=" + offset + "-");
    }

    private static BufferedInputStream createBufferedInputStream(URLConnection connection) throws IOException {
        return new BufferedInputStream(connection.getInputStream());
    }

    private static BufferedOutputStream createBufferedOutputStream(Path destinationFile) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(destinationFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
    }
}
