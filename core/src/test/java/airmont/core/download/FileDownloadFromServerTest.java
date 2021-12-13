package airmont.core.download;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Disabled
public class FileDownloadFromServerTest {

    @Test
    void download() throws Exception {
        URL url = new URL("https", "<host>", "/<file>");
        Path file = Files.createTempFile("tmp", "");
        file.toFile().deleteOnExit();
        Path dir = file.getParent();
        FileDownloader fileDownloader = new FileDownloader();
        file = fileDownloader.download(url, dir, new TimeAndSizeTrackerAdapter(new CmdTracker()) {
            int count = 0;

            @Override
            public void read(int bytesRead) {
                super.read(bytesRead);
                count += 1;
                if (count > 10000) {
                    fileDownloader.stop();
                }
            }
        });
        new FileDownloader().download(url, file, new TimeAndSizeTrackerAdapter(new CmdTracker()));
        file.toFile().deleteOnExit();
    }
}
