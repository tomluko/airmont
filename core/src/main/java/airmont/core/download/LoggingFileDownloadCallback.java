package airmont.core.download;

import airmont.core.connection.UrlConnectionHeader;

import java.net.URL;
import java.nio.file.Path;

public record LoggingFileDownloadCallback(Logger logger) implements FileDownloadCallback {

    @Override
    public void before(URL url, Path destinationFile) {
        logger.out("Url: " + url.toString());
        logger.out("Destination file: " + destinationFile.toString());
    }

    @Override
    public void start(UrlConnectionHeader header) {
        logger.out("Starting ...");
        logger.out("Header: " + header.toString().replace("\n", ";"));
    }

    @Override
    public void resume(long fileSizeInBytes, UrlConnectionHeader header) {
        logger.out("Resuming ...");
        logger.out("Header: " + header.toString().replace("\n", ";"));
    }

    @Override
    public void read(int bytesRead) {
        // not very interesting
    }

    @Override
    public void exception(Exception e) {
        logger.err(e);
    }

    @Override
    public void finish(boolean stopped) {
        logger.out("Finishing ...");
        logger.out("Was Stopped: " + stopped);
    }
}
