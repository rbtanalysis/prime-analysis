package org.rbt.primeanalysis.ui.control;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.util.Config;

/**
 *
 * @author rbtuc
 */
public class DomainEntry extends HBox {

    private DoubleEntryPane lowerBound;
    private DoubleEntryPane upperBound;

    public DomainEntry(final PrimeAnalysis app, Double lowerValue, Double upperValue) {
        lowerBound = new DoubleEntryPane("Lower Bound: ", lowerValue);
        upperBound = new DoubleEntryPane("Upper Bound: ", upperValue, 100.0);

        getChildren().add(lowerBound);
        getChildren().add(upperBound);

        Button b = new Button("Set Bounds and Reload");

        b.setOnAction(e -> {
            e.consume();
            Platform.runLater(() -> {
                Config newConfig = app.getConfig().clone();
                newConfig.setLowerBound(lowerBound.toDouble());
                newConfig.setUpperBound(upperBound.toDouble());
                app.load(newConfig, "Applying boundry updates and reloading...", true);
            });
        });

        getChildren().add(b);

        b = new Button("Reset to Default");

        b.setOnAction(e -> {
            e.consume();
            Platform.runLater(() -> {
                Config newConfig = app.getConfig().clone();
                newConfig.setLowerBound(null);
                newConfig.setUpperBound(null);
                app.load(newConfig, "Applying boundry updates and reloading...", true);
            });
        });

        getChildren().add(b);
        this.setSpacing(5.0);
        this.setMinHeight(30);

        setPadding(new Insets(10, 10, 10, 10));
        this.setStyle("-fx-border-color: darkslategrey;");
    }

}
