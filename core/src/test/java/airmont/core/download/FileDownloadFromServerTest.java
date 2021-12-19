package airmont.core.download;

import airmont.core.TempFileTestCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;

@Disabled
public class FileDownloadFromServerTest extends TempFileTestCase {

    @Test
    void download() throws Exception {
        URL url = new URL("https", "<host>", "/<file>");
        Path dir = getTempDir();
        FileDownloader fileDownloader = new FileDownloader();
        Path file = fileDownloader.download(url, dir, new TimeAndSizeTrackerAdapter(new CmdTracker()) {
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
