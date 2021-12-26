package airmont.core.download;

import airmont.core.connection.UrlConnectionHeader;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CombinedFileDownloadCallback implements FileDownloadCallback {

    private final List<FileDownloadCallback> callbacks = new ArrayList<>();

    public CombinedFileDownloadCallback add(FileDownloadCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
        return this;
    }

    @Override
    public void before(URL url, Path destinationFile) {
        callbacks.forEach(callback -> callback.before(url, destinationFile));
    }

    @Override
    public void start(UrlConnectionHeader header) {
        callbacks.forEach(callback -> callback.start(header));
    }

    @Override
    public void resume(long fileSizeInBytes, UrlConnectionHeader header) {
        callbacks.forEach(callback -> callback.resume(fileSizeInBytes, header));
    }

    @Override
    public void read(int bytesRead) {
        callbacks.forEach(callback -> callback.read(bytesRead));
    }

    @Override
    public void exception(Exception e) {
        callbacks.forEach(callback -> callback.exception(e));
    }

    @Override
    public void finish(boolean stopped) {
        callbacks.forEach(callback -> callback.finish(stopped));
    }
}
