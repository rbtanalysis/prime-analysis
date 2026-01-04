package org.rbt.primeanalysis.ui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;

public class ZoomHandler {

    public ZoomHandler() {
    }

    public void zoom(Node node, double factor, double x, double y) {
        // determine scale
        double oldScale = node.getScaleX();
        double scale = oldScale * factor;

        if (scale > 0.2) {
            double f = (scale / oldScale) - 1;

            // determine offset that we will have to move the node
            Bounds bounds = node.localToScene(node.getBoundsInLocal());
            double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
            double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

            node.setTranslateX(node.getTranslateX() - f * dx);
            node.setTranslateY(node.getTranslateY() - f * dy);
            node.setScaleX(scale);
            node.setScaleY(scale);
        }
    }

    public void clear(Node node) {
        Platform.runLater(() -> {
            node.setTranslateX(0.0);
            node.setTranslateY(0.0);
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });

    }
}
