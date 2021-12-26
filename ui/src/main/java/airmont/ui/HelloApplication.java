package airmont.ui;

import airmont.core.FileDownloadManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private FileDownloadManager downloadManager;

    @Override
    public void start(Stage stage) throws Exception {

        downloadManager = new FileDownloadManager();
        downloadManager.startServer();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        downloadManager.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}