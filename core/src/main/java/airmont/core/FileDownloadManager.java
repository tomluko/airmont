package airmont.core;

import airmont.core.download.*;
import airmont.core.downloadtarget.DownloadTarget;
import airmont.core.downloadtarget.DownloadTargetMetaInformation;
import airmont.core.server.DownloadEndpoint;
import airmont.core.server.DownloadServers;
import airmont.core.server.Server;
import airmont.core.settings.Settings;

import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadManager {

    private final Logger logger;
    private final Settings settings;
    private final FileDownloadCallback callback;
    private final MultiFileDownloader downloader;
    private Server server;

    public FileDownloadManager() {
        this(new EmptyFileDownloadCallback());
    }

    public FileDownloadManager(FileDownloadCallback callback) {
        this(Paths.get("."), callback);
    }

    public FileDownloadManager(Path settingsDir, FileDownloadCallback callback) {
        logger = new Logger("DownloadManager");
        settings = new Settings(settingsDir);
        logger.out("Settings");
        logger.out("Property: downloads dir: " + settings.getDownloadDir());
        logger.out("Property: server port: " + settings.getServerPort());
        logger.out("Property: simultaneous downloads count: " + settings.getSimultaneousDownloadsCount());
        this.callback = callback;
        downloader = new MultiFileDownloader(settings.getSimultaneousDownloadsCount());
        logger.out("Resuming all downloads ...");
        resumeDownloads();
        logger.out("All downloads are resumed");
    }

    private void resumeDownloads() {
        DownloadTarget.findAll(settings.getDownloadDir()).forEach(this::resumeDownload);
    }

    private void resumeDownload(DownloadTarget downloadTarget) {
        DownloadTargetMetaInformation parameters = downloadTarget.getParameters();
        URL url = parameters.getUrl();
        Path destinationFile = parameters.getDestinationFile();
        download(url, destinationFile, callback);
    }

    public void startServer() throws Exception {
        logger.out("Starting server ...");
        int serverPort = settings.getServerPort();
        InetAddress localHost = InetAddress.getLocalHost();
        logger.out("Endpoint: add download: http://" + localHost.getHostName() + ":" + serverPort +
                DownloadEndpoint.ENDPOINT + "?" + DownloadEndpoint.URL + "=<https://remote.file>");
        logger.out("Endpoint: add download: http://" + localHost.getHostAddress() + ":" + serverPort +
                DownloadEndpoint.ENDPOINT + "?" + DownloadEndpoint.URL + "=<https://remote.file>");
        server = DownloadServers.create(url -> download(url, settings.getDownloadDir(), callback), serverPort);
        server.start();
        logger.out("Server is started");
    }

    private void download(URL url, Path path, FileDownloadCallback callback) {
        Logger logger = new Logger(url.toString());
        downloader.download(url, path, new CombinedFileDownloadCallback()
                .add(callback)
                .add(new TimeAndSizeTrackerAdapter(new LoggingTimeAndSizeTracker(logger)))
                .add(new LoggingFileDownloadCallback(logger)));
    }

    public void stop() {
        logger.out("Stopping server ...");
        if (server != null) {
            server.stop();
        }
        logger.out("Server is Stopped");
        logger.out("Stopping all downloads ...");
        downloader.stopAllDownloads();
        logger.out("All downloads are stopped");
    }
}
