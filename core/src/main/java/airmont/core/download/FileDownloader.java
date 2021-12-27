package airmont.core.download;

import airmont.core.connection.AutoClosableHttpURLConnection;
import airmont.core.connection.Connections;
import airmont.core.connection.UrlConnectionHeader;
import airmont.core.downloadtarget.DownloadTarget;
import airmont.core.downloadtarget.DownloadTargetMetaInformation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        DownloadTargetMetaInformation metaInformation = new DownloadTargetMetaInformation().setUrl(url);
        long fileSize = DownloadTarget.getFileSize(destination);
        if (fileSize > 0) {
            connection.addResumeRequestProperty(fileSize);
            connection.verifyResponseCode(AutoClosableHttpURLConnection.EXPECTED_RESPONSE_CODE_RESUME_DOWNLOAD);
            UrlConnectionHeader header = connection.getHeader();
            callback.resume(fileSize, header);
            metaInformation
                    .setMetaInfo(header.toString())
                    .setDestinationFile(destination);
        } else {
            connection.verifyResponseCode(AutoClosableHttpURLConnection.EXPECTED_RESPONSE_CODE_START_DOWNLOAD);
            UrlConnectionHeader header = connection.getHeader();
            callback.start(header);
            Path destinationFile = Files.isDirectory(destination) ?
                    Paths.get(destination.toAbsolutePath().toString(), getFileName(url, header).toString()) :
                    destination;
            metaInformation
                    .setMetaInfo(header.toString())
                    .setDestinationFile(destinationFile);
        }
        DownloadTarget downloadTarget = new DownloadTarget(metaInformation);
        try (BufferedInputStream in = connection.createBufferedInputStream();
             BufferedOutputStream out = downloadTarget.createBufferedOutputStream()) {
            copy(callback, in, out);
        } catch (Exception e) {
            callback.exception(e);
        } finally {
            callback.finish(stop);
            if (!stop) {
                downloadTarget.getParameters().deleteFile();
            }
        }
        return metaInformation.getDestinationFile();
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
        return "download" + System.currentTimeMillis();
    }

    private void copy(FileDownloadCallback callback, BufferedInputStream in, BufferedOutputStream out) throws IOException {
        int bytesRead;
        try {
            while ((bytesRead = in.read(dataBuffer, 0, DATA_BUFFER_SIZE)) != -1 && !stop) {
                out.write(dataBuffer, 0, bytesRead);
                callback.read(bytesRead);
            }
        } finally {
            out.flush();
        }
    }

    public void stop() {
        stop = true;
    }

}
