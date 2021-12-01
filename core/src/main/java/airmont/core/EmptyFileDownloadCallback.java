package airmont.core;

import java.net.URL;
import java.nio.file.Path;

class EmptyFileDownloadCallback implements FileDownloadCallback {
    @Override
    public void before(URL url, Path destinationFile) {
        // do nothing
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void resume() {
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
    public void finish(boolean stop) {
        // do nothing
    }
}
