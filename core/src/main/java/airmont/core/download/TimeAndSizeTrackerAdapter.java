package airmont.core.download;

import airmont.core.connection.UrlConnectionHeader;

import java.net.URL;
import java.nio.file.Path;

public class TimeAndSizeTrackerAdapter implements FileDownloadCallback {

    private final TimeAndSizeTracker tracker;

    private long startTime = 0;
    private long lastTime = 0;
    private long bytes = 0;
    private long time;

    TimeAndSizeTrackerAdapter(TimeAndSizeTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void before(URL url, Path destinationFile) {

    }

    @Override
    public void start(UrlConnectionHeader header) {
        startTime = System.currentTimeMillis();
        lastTime = startTime;
        tracker.start(0, header.getFileSize(), startTime);
    }

    @Override
    public void resume(long fileSizeInBytes, UrlConnectionHeader header) {
        startTime = System.currentTimeMillis();
        lastTime = startTime;
        tracker.start(fileSizeInBytes, header.getFileSize(), startTime);
    }

    @Override
    public void read(int bytesRead) {
        long currentTime = System.currentTimeMillis();
        bytes += bytesRead;
        time += currentTime - lastTime;
        lastTime = currentTime;
        if (time >= 1000) {
            tracker.step(bytes, time);
            bytes = 0;
            time = 0;
        }
    }

    @Override
    public void exception(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void finish(boolean stopped) {
        long millis = System.currentTimeMillis() - startTime;
        tracker.finish(millis);
    }
}
