package airmont.core.download;

import java.text.DecimalFormat;

public class CmdTracker implements TimeAndSizeTracker {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        DECIMAL_FORMAT.setGroupingUsed(false);
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
    }

    @Override
    public void start(long startingSizeInBytes, long fullSizeInBytes, long startingTime) {
        System.out.println("Starting size: " + format(bytesToKilobytes(startingSizeInBytes)) + " KB");
        System.out.println("Full size: " + format(bytesToKilobytes(fullSizeInBytes)) + " KB");
    }

    @Override
    public void step(long bytesRead, long time) {
        System.out.println("Speed: " + format(bytesToKilobytes(bytesRead) / millisToSeconds(time)) + " KB/s");
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
