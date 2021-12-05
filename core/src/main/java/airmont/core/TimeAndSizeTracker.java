package airmont.core;

public interface TimeAndSizeTracker {
    /**
     * @param startingSizeInBytes in bytes
     * @param fullSizeInBytes     in bytes
     * @param startingTime        in millis
     */
    void start(long startingSizeInBytes, long fullSizeInBytes, long startingTime);

    /**
     * @param bytesRead in bytes
     * @param time      in millis
     */
    void step(long bytesRead, long time);

    /**
     * @param duration in millis
     */
    void finish(long duration);
}
