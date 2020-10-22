module Soundboard {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.desktop;
    requires json.simple;
    requires jnativehook;

    opens norsker to javafx.fxml, javafx.graphics, javafx.controls;

    exports norsker.gui;
}