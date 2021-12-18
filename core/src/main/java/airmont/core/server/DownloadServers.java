package airmont.core.server;

public class DownloadServers {

    public static Server create(DownloadHandler downloadHandler, int port) {
        Server server = new Server(port);
        server.add(new DownloadEndpoint(downloadHandler));
        return server;
    }
}
