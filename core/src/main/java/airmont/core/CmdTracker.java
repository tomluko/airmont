package airmont.core;

import java.text.DecimalFormat;

public class CmdTracker implements TimeAndSizeTracker {

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
