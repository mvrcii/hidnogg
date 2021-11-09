/**
 *
 */
module stickfight2d {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    requires javafx.swing;
    requires tinysound;
    opens stickfight2d to javafx.graphics;
    exports stickfight2d;
}