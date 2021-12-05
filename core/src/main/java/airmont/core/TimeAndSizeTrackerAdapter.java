package airmont.core;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TimeAndSizeTrackerAdapter implements FileDownloadCallback {

    private final TimeAndSizeTracker tracker;

    long startTime = 0;
    long lastTime = 0;
    long bytes = 0;

    TimeAndSizeTrackerAdapter(TimeAndSizeTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void before(URL url, Path destinationFile) {

    }

    @Override
    public void start(Map<String, List<String>> headerFields) {
        startTime = System.currentTimeMillis();
        lastTime = startTime;
        tracker.start(0, getFullFileSize(headerFields), startTime);
    }

    @Override
    public void resume(long fileSizeInBytes, Map<String, List<String>> headerFields) {
        startTime = System.currentTimeMillis();
        lastTime = startTime;
        tracker.start(fileSizeInBytes, getFullFileSize(headerFields), startTime);
    }

    @Override
    public void read(int bytesRead) {
        bytes += bytesRead;
        long time = System.currentTimeMillis() - lastTime;
        if (time >= 1000) {
            tracker.step(bytes, time);
            lastTime = System.currentTimeMillis();
            bytes = 0;
        }
    }

    @Override
    public void exception(Exception e) {

    }

    @Override
    public void finish(boolean stopped) {
        long millis = System.currentTimeMillis() - startTime;
        tracker.finish(millis);
    }

    private long getFullFileSize(Map<String, List<String>> headerFields) {
        List<String> fullFileSizes = headerFields.get("Content-Length");
        if (fullFileSizes == null || fullFileSizes.isEmpty()) {
            return 0;
        }
        String fullFileSizeText = fullFileSizes.get(0);
        return Long.parseLong(fullFileSizeText);
    }
}
