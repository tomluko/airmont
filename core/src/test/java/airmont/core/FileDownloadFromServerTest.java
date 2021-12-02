package airmont.core;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class FileDownloadFromServerTest {

    @Test
    void download() throws Exception {
        URL url = new URL("https", "<host>", "<file>");
        Path file = Files.createTempFile("downloadTest", "rar");
        FileDownloader fileDownloader = new FileDownloader();
        fileDownloader.download(url, file, new InfoCallback(new InformativeTracker()) {
            int count = 0;

            @Override
            public void read(int bytesRead) {
                super.read(bytesRead);
                count += 1;
                if (count > 1000) {
                    fileDownloader.stop();
                }
            }
        });
        new FileDownloader().download(url, file, new InfoCallback(new InformativeTracker()));
    }

    static class InfoCallback implements FileDownloadCallback {

        private final TimeAndSizeTracker tracker;

        long startTime = 0;
        long lastTime = 0;
        long bytes = 0;

        InfoCallback(TimeAndSizeTracker tracker) {
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
        public void finish(boolean stop) {
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

    static class InformativeTracker implements TimeAndSizeTracker {

        private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

        static {
            DECIMAL_FORMAT.setGroupingUsed(false);
            DECIMAL_FORMAT.setMaximumFractionDigits(2);
        }

        @Override
        public void start(long startingSize, long fullSize, long startingTime) {
            System.out.println("Starting size: " + format(bytesToKilobytes(startingSize)) + " KB");
            System.out.println("Full size: " + format(bytesToKilobytes(fullSize)) + " KB");
        }

        @Override
        public void step(long bytes, long time) {
            System.out.println("Speed: " + format(bytesToKilobytes(bytes) / millisToSeconds(time)) + " KB/s");
        }

        @Override
        public void finish(long duration) {
            System.out.println("Download took: " + format(millisToSeconds(duration)) + " s to complete");
        }

        private double bytesToKilobytes(double bytes) {
            return bytes / 1024;
        }

        private double millisToSeconds(long millis) {
            return (double) millis / 1000;
        }

        private String format(double number) {
            return DECIMAL_FORMAT.format(number);
        }
    }


    interface TimeAndSizeTracker {
        /**
         * @param startingSize in bytes
         * @param fullSize     in bytes
         * @param startingTime in millis
         */
        void start(long startingSize, long fullSize, long startingTime);

        /**
         * @param bytes in bytes
         * @param time  in millis
         */
        void step(long bytes, long time);

        /**
         * @param duration in millis
         */
        void finish(long duration);
    }


}
