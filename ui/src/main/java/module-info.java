module airmont.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires airmont.core;

    opens airmont.ui to javafx.fxml;
    exports airmont.ui;
}