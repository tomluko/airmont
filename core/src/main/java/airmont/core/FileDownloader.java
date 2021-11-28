package airmont.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDownloader {

    public static void download(URL url, Path destinationFile) throws Exception {
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(destinationFile))) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
        }
    }
}
