package com.PK2D.PK2D_Mav.GUI;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class PositionLabel {
    private static Label label;

    public static void addToPane(Pane pane) {
        label = new Label();
        pane.getChildren().add(label);
    }

    public static void set(String message) {
        label.setText(message);
    }

}
