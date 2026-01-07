package org.rbt.primeanalysis.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.util.Config;
import org.rbt.primeanalysis.util.Constants;

/**
 *
 * @author rbtuc
 */
public class ConfigurationTab extends Tab {
    private final Config newConfig;
    private IntegerEntryPane fileCount;
    private IntegerEntryPane scale;

    public ConfigurationTab(PrimeAnalysis app) {
        super("Configuration");
        newConfig = app.getConfig().clone();

        VBox vbox = new VBox();

        fileCount = new IntegerEntryPane("Prime File Count:",  newConfig.getPrimeFileLoadCount());
        vbox.getChildren().add(fileCount);
        scale = new IntegerEntryPane("Big Decimal Scale:", newConfig.getBigDecimalScale().getScale());
        vbox.getChildren().add(scale);

        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.setSpacing(5.0);

        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.setSpacing(5.0);

        Button b = new Button("Set Configuration and Reload");

        b.setOnAction(e -> {
            newConfig.setPrimeFileLoadCount(fileCount.toInteger());
            newConfig.getBigDecimalScale().setScale(scale.toInteger());
            app.load(newConfig, "Applying configuration updates and reloading...", true);
        });

        setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (isSelected()) {
                    Config config = app.getConfig().clone();
                    fileCount.setText(config.getPrimeFileLoadCount());
                    scale.setText(config.getBigDecimalScale());
                }
            }
        });

        FlowPane buttonPane = new FlowPane();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPadding(new Insets(20, 20, 20, 20));
        buttonPane.getChildren().add(b);
        vbox.getChildren().add(buttonPane);
 
        setContent(vbox);
    }



}
