package airmont.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import airmont.core.SomeApi;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        new SomeApi();
    }
}