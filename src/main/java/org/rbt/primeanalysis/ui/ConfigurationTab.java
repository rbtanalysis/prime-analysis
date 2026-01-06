package org.rbt.primeanalysis.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.util.Config;
import org.rbt.primeanalysis.util.MinMaxHolder;

/**
 *
 * @author rbtuc
 */
public class ConfigurationTab extends Tab {

    private static final Double DEFAULT_LABEL_WIDTH = 150.0;
    private final Config newConfig;
    private Map<String, Control> entryFieldMap = new HashMap();
    private PrimeAnalysis app = null;
    private VBox gaps = null;

    public ConfigurationTab(PrimeAnalysis app) {
        super("Configuration");
        this.app = app;
        newConfig = app.getConfig().clone();

        VBox vbox = new VBox();

        vbox.getChildren().add(getEntryPane("primeFileCount", "Prime File Count:", newConfig.getPrimeFileLoadCount(), DEFAULT_LABEL_WIDTH));
        vbox.getChildren().add(getEntryPane("bigDecimalScale", "Big Decimal Scale:", newConfig.getBigDecimalScale().getScale(), DEFAULT_LABEL_WIDTH));

        int cnt = 1;
        for (MinMaxHolder mm : newConfig.getRanges()) {
            vbox.getChildren().add(getEntryPane(mm, cnt));
            cnt++;
        }

        vbox.getChildren().add(getCheckBoxPane("useLogForCounts", "Apply ln(count) for counts", newConfig.isUseLogForCounts(), DEFAULT_LABEL_WIDTH));
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.setSpacing(5.0);

        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.setSpacing(5.0);

        Button b = new Button("Set Configuration and Reload");

        b.setOnAction(e -> {
            TextField tf = (TextField) entryFieldMap.get("primeFileCount");
            newConfig.setPrimeFileLoadCount(Integer.valueOf(tf.getText()));

            tf = (TextField) entryFieldMap.get("bigDecimalScale");
            newConfig.getBigDecimalScale().setScale(Integer.valueOf(tf.getText()));

            int indx = 1;
            for (MinMaxHolder mm : newConfig.getRanges()) {
                tf = (TextField) entryFieldMap.get("range" + indx + "min");
                mm.setMin(tf.getText());

                tf = (TextField) entryFieldMap.get("range" + indx + "max");
                mm.setMax(tf.getText());
                indx++;
            }

            CheckBox cb = (CheckBox) entryFieldMap.get("useLogForCounts");
            newConfig.setUseLogForCounts(cb.isSelected());

             Set<Integer> selectedGaps = new HashSet();

            for (Node node : gaps.getChildren()) {
                cb = (CheckBox) node;
                if (cb.isSelected()) {
                    selectedGaps.add(Integer.valueOf(cb.getText()));
                }
            }

            newConfig.setSelectedGaps(selectedGaps);

            app.load(newConfig, "Applying configuration updates and reloading...", true);
        });

        setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (isSelected()) {
                    Config config = app.getConfig().clone();
                    ((TextField) entryFieldMap.get("primeFileCount")).setText(config.getPrimeFileLoadCount().toString());
                    ((TextField) entryFieldMap.get("bigDecimalScale")).setText(config.getBigDecimalScale().toString());

                    int cnt = 1;
                    for (MinMaxHolder mm : newConfig.getRanges()) {
                        ((TextField) entryFieldMap.get("range" + cnt + "min")).setText(mm.getMin().toString());
                        ((TextField) entryFieldMap.get("range" + cnt + "max")).setText(mm.getMax().toString());
                        cnt++;
                    }

                    ((CheckBox) entryFieldMap.get("useLogForCounts")).setSelected(config.isUseLogForCounts());
 
                }
            }
        });

        FlowPane buttonPane = new FlowPane();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPadding(new Insets(20, 20, 20, 20));
        buttonPane.getChildren().add(b);

        BorderPane gapPane = new BorderPane();
        gapPane.setPrefSize(200, 300);

        gaps = new VBox();

        for (Integer gap : app.getPrimeGapSet()) {
            CheckBox cb = new CheckBox(gap.toString());
            cb.setSelected(app.getPrimeGapSet().contains(gap));
            gaps.getChildren().add(cb);
        }

        ScrollPane sp = new ScrollPane(gaps);
 
        gapPane.setCenter(sp);
        FlowPane fp = new FlowPane();
        fp.getChildren().add(b = new Button("Select All"));

        b.setOnAction(e -> {
            selectAllGaps();
        });
        fp.getChildren().add(b = new Button("Clear All"));

        b.setOnAction(e -> {
            deSelectAllGaps();
        });

        gapPane.setBottom(fp);
        HBox hbox = new HBox();
        hbox.setSpacing(5.0);
        Label l = new Label("Prime Gaps:");
        l.setPrefWidth(DEFAULT_LABEL_WIDTH);
        l.setAlignment(Pos.CENTER_RIGHT);

        hbox.getChildren().add(l);
        hbox.getChildren().add(gapPane);
        vbox.getChildren().add(new Label(""));
        vbox.getChildren().add(hbox);

        BorderPane bp = new BorderPane(vbox);
        bp.setBottom(buttonPane);
        setContent(bp);
    }

    UnaryOperator<Change> numberFilter = change -> {
        // Get the new text resulting from the change
        String newText = change.getControlNewText();
        // Check if the new text contains only digits
        if (newText.matches("\\d*") || isValidDecimalPoint(newText)) {
            return change; // Accept the change
        } else {
            return null; // Reject the change
        }
    };

    private HBox getCheckBoxPane(String name, String label, Boolean selected, Double labelWidth) {
        HBox retval = new HBox();
        Label l = new Label("");
        l.setPrefWidth(labelWidth);
        retval.getChildren().add(l);
        CheckBox cb = new CheckBox(label);
        cb.setSelected(selected);
        entryFieldMap.put(name, cb);

        retval.getChildren().add(cb);
        retval.setSpacing(5.0);
        return retval;
    }

    private HBox getEntryPane(String name, String label, Object curval, Double labelWidth) {
        HBox retval = new HBox();
        Label l = new Label(label);
        l.setPrefWidth(labelWidth);
        l.setAlignment(Pos.CENTER_RIGHT);
        retval.getChildren().add(l);
        TextField tf = new TextField(curval.toString());
        entryFieldMap.put(name, tf);

        tf.setTextFormatter(new TextFormatter<>(numberFilter));
        tf.setPrefColumnCount(10);
        retval.getChildren().add(tf);
        retval.setSpacing(5.0);
        return retval;
    }

    private HBox getEntryPane(MinMaxHolder mm, Integer indx) {
        HBox retval = new HBox();

        retval.getChildren().add(getEntryPane("range" + indx + "min", "Radian range " + indx + " Min:", mm.getMin(), DEFAULT_LABEL_WIDTH));
        retval.getChildren().add(getEntryPane("range" + indx + "max", "Max:", mm.getMax(), 50.0));

        return retval;
    }

    private boolean isValidDecimalPoint(String in) {
        boolean retval = false;
        int indx = in.indexOf(".");
        if (indx > 0) {
            retval = (in.indexOf(".", indx + 1) < 0);
        }

        return retval;
    }

    private void selectAllGaps() {
        newConfig.getSelectedGaps().clear();
        for (Integer gap : app.getPrimeGapSet()) {
            newConfig.getSelectedGaps().add(gap);
        }

        for (Node node : gaps.getChildren()) {
            ((CheckBox) node).setSelected(true);
        }

    }

    private void deSelectAllGaps() {
        newConfig.getSelectedGaps().clear();
        for (Node node : gaps.getChildren()) {
            ((CheckBox) node).setSelected(false);
        }
    }

}
