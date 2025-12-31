package org.rbt.primeanalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    
    public ConfigurationTab(PrimeAnalysis app) {
        super("Configuration");
        newConfig = app.getConfig().clone();

        VBox vbox = new VBox();

        vbox.getChildren().add(getEntryPane("primeFileCount", "Prime File Count:", newConfig.getPrimeFileLoadCount(), DEFAULT_LABEL_WIDTH));
        vbox.getChildren().add(getEntryPane("bigDecimalScale", "Big Decimal Scale:", newConfig.getBigDecimalScale().getScale(), DEFAULT_LABEL_WIDTH));
      
        int cnt = 1;
        for (MinMaxHolder mm : newConfig.getRanges()) {
            vbox.getChildren().add(getEntryPane(mm, cnt));
            cnt++;
        }

        vbox.getChildren().add(getCheckBoxPane("useLog", "Apply ln(count) for counts", newConfig.isUseLog(), DEFAULT_LABEL_WIDTH));
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setAlignment(Pos.BASELINE_RIGHT);
        vbox.setSpacing(5.0);
        FlowPane fp = new FlowPane();
        fp.setAlignment(Pos.CENTER);

        Button b = new Button("Set Configuration and Reload");

        b.setOnAction(e -> {
            TextField tf = (TextField) entryFieldMap.get("primeFileCount");
            newConfig.setPrimeFileLoadCount(Integer.valueOf(tf.getText()));

            tf = (TextField) entryFieldMap.get("bigDecimalScale");
            newConfig.getBigDecimalScale().setScale(Integer.valueOf(tf.getText()));

            int indx = 1;
            for (MinMaxHolder mm : newConfig.getRanges()) {
                tf = (TextField) entryFieldMap.get("range" + indx + "min");
                mm.setMin(app.getUtil().toBigDecimal(tf.getText()));

                tf = (TextField) entryFieldMap.get("range" + indx + "max");
                mm.setMax(app.getUtil().toBigDecimal(tf.getText()));
                indx++;
            }

            CheckBox cb = (CheckBox) entryFieldMap.get("useLog");
            newConfig.setUseLog(cb.isSelected());

            app.load(newConfig, "Applying configuration updates...");
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

                    ((CheckBox) entryFieldMap.get("useLog")).setSelected(config.isUseLog());
                }
            }
        });

        fp.getChildren().add(b);
        vbox.getChildren().add(fp);
        setContent(new BorderPane(vbox));
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
}
