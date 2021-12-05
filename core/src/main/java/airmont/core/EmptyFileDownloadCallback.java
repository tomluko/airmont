package airmont.core;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

class EmptyFileDownloadCallback implements FileDownloadCallback {
    @Override
    public void before(URL url, Path destinationFile) {
        // do nothing
    }

    @Override
    public void start(Map<String, List<String>> headerFields) {
        // do nothing
    }

    @Override
    public void resume(long fileSizeInBytes, Map<String, List<String>> headerFields) {
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
