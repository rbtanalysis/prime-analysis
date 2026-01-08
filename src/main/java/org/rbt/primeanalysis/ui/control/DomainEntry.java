package org.rbt.primeanalysis.ui.control;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.util.Config;
import org.rbt.primeanalysis.util.Constants;

/**
 *
 * @author rbtuc
 */
public class DomainEntry extends HBox {

    private DoubleEntryPane lowerBound;
    private DoubleEntryPane upperBound;
    private IntegerEntryPane decimalScale;

    public DomainEntry(final PrimeAnalysis app, Double lowerValue, Double upperValue, Integer scale) {
        lowerBound = new DoubleEntryPane("Lower Bound: ", lowerValue);
        upperBound = new DoubleEntryPane("Upper Bound: ", upperValue, 100.0);
        decimalScale = new IntegerEntryPane("Scale: ", scale, 75.0);

        getChildren().add(lowerBound);
        getChildren().add(upperBound);
        getChildren().add(decimalScale);

        Button b = new Button("Update Settings and Reload");

        b.setOnAction(e -> {
            e.consume();
            Config newConfig = app.getConfig().clone();
            newConfig.setLowerBound(lowerBound.toDouble());
            newConfig.setUpperBound(upperBound.toDouble());
            newConfig.getBigDecimalScale().setScale(decimalScale.toInteger());
            app.load(newConfig, "Applying updates and reloading...", true);
        });

        getChildren().add(b);

        b = new Button("Reset to Defaults");

        b.setOnAction(e -> {
            e.consume();
            Config newConfig = app.getConfig().clone();
            newConfig.setLowerBound(null);
            newConfig.setUpperBound(null);
            newConfig.getBigDecimalScale().setScale(Constants.DEFAULT_SCALE);
            app.load(newConfig, "Applying updates and reloading...", true);
        });

        getChildren().add(b);
        this.setSpacing(5.0);
        this.setMinHeight(30);

        setPadding(new Insets(10, 10, 10, 10));
        this.setStyle("-fx-border-color: darkslategrey;");
    }

}
