package airmont.core;

public interface TimeAndSizeTracker {
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
