package airmont.core;

public class Airmont {

    public static void main(String[] args) throws Exception {
        FileDownloadManager downloadManager = new FileDownloadManager();
        downloadManager.startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(downloadManager::stop));
    }
}
