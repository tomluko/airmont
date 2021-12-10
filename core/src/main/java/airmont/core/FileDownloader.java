package airmont.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class FileDownloader {

    private static final int EXPECTED_RESPONSE_CODE_RESUME_DOWNLOAD = 206;
    private static final int EXPECTED_RESPONSE_CODE_START_DOWNLOAD = 200;

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
    public Path download(URL url, Path destinationFile) throws IOException {
        return download(url, destinationFile, new EmptyFileDownloadCallback());
    }

    /**
     * @param url         http, https url
     * @param destination where to save bytes or dir to create new file
     * @throws IOException if fails to start the download
     */
    public Path download(URL url, Path destination, FileDownloadCallback callback) throws IOException {
        callback.before(url, destination);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Path destinationFile;
        long fileSize = getFileSize(destination);
        if (fileSize > 0) {
            addResumeRequestProperty(connection, fileSize);
            verifyResponseCode(connection, EXPECTED_RESPONSE_CODE_RESUME_DOWNLOAD);
            callback.resume(fileSize, connection.getHeaderFields());
            destinationFile = destination;
        } else {
            verifyResponseCode(connection, EXPECTED_RESPONSE_CODE_START_DOWNLOAD);
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            callback.start(headerFields);
            if (Files.isDirectory(destination)) {
                destinationFile = Paths.get(destination.toAbsolutePath().toString(), getFileName(url, headerFields).toString());
                if (!Files.exists(destinationFile)) {
                    Files.createFile(destinationFile);
                }
            } else {
                destinationFile = destination;
            }
        }
        try (BufferedInputStream in = createBufferedInputStream(connection);
             BufferedOutputStream out = createBufferedOutputStream(destinationFile)) {
            copy(callback, in, out);
        } catch (Exception e) {
            callback.exception(e);
        } finally {
            callback.finish(stop);
        }
        return destinationFile;
    }

    private Path getFileName(URL url, Map<String, List<String>> headerFields) {
        String fileName;
        fileName = getFileNameFromContentDisposition(headerFields);
        if (fileName == null) {
            fileName = getFileNameFromUrl(url);
        }
        if (fileName == null) {
            fileName = createFileName();
        }
        return Paths.get(fileName);
    }

    private String getFileNameFromContentDisposition(Map<String, List<String>> headerFields) {
        List<String> contentDisposition = headerFields.get("Content-Disposition");
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            return null;
        }
        for (String cd : contentDisposition) {
            String[] attributes = cd.split(";");
            for (String attribute : attributes) {
                if (attribute.toLowerCase().contains("filename")) {
                    try {
                        return attribute.substring(attribute.indexOf('\"') + 1, attribute.lastIndexOf('\"'));
                    } catch (Exception e) {
                        return attribute.substring(attribute.indexOf('=') + 1);
                    }
                }
            }
        }
        return null;
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
