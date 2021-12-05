package airmont.core;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface FileDownloadCallback {

    /**
     * before download process begins
     */
    void before(URL url, Path destinationFile);

    /**
     * file does not exist, start new download
     */
    void start(Map<String, List<String>> headerFields);

    /**
     * file exists, resume download
     */
    void resume(long fileSizeInBytes, Map<String, List<String>> headerFields);

    /**
     * one read iteration complete with given amount of bytes
     */
    void read(int bytesRead);

    /**
     * download interrupted with an exception
     */
    void exception(Exception e);

    /**
     * download process is done
     *
     * @param stopped true if download was stopped, false if completed
     */
    void finish(boolean stopped);
}
