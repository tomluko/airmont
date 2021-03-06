package airmont.core.download;

import airmont.core.connection.UrlConnectionHeader;

import java.net.URL;
import java.nio.file.Path;

public class EmptyFileDownloadCallback implements FileDownloadCallback {
    @Override
    public void before(URL url, Path destinationFile) {
        // do nothing
    }

    @Override
    public void start(UrlConnectionHeader header) {
        // do nothing
    }

    @Override
    public void resume(long fileSizeInBytes, UrlConnectionHeader header) {
        // do nothing
    }

    @Override
    public void read(int bytesRead) {
        // do nothing
    }

    @Override
    public void exception(Exception e) {
        // do nothing
    }

    @Override
    public void finish(boolean stopped) {
        // do nothing
    }
}
