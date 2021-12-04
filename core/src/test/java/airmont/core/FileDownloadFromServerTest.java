package airmont.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDownloadFromServerTest {

    @Test
    @Disabled
    void download() throws Exception {
        URL url = new URL("https", "<host>", "/<file>");
        Path file = Files.createTempFile("downloadTest", "rar");
        FileDownloader fileDownloader = new FileDownloader();
        fileDownloader.download(url, file, new TimeAndSizeTrackerAdapter(new CmdTracker()) {
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
        new FileDownloader().download(url, file, new TimeAndSizeTrackerAdapter(new CmdTracker()));
    }
}
