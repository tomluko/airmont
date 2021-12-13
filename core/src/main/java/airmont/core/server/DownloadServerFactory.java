package airmont.core.server;

public class DownloadServerFactory {

    static final int PORT = 32582;

    public static Server create(DownloadHandler downloadHandler) {
        Server server = new Server(PORT);
        server.add(new DownloadEndpoint(downloadHandler));
        return server;
    }
}
