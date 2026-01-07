package org.rbt.primeanalysis.ui;

import java.util.function.UnaryOperator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;
import org.rbt.primeanalysis.util.Constants;

/**
 *
 * @author rbtuc
 */
public class EntryPane <k> extends HBox {
    private TextField entryField;
    private Label entryName;
    
    public EntryPane(String name, String label) {
        this(label, null, Constants.DEFAULT_LABEL_WIDTH);
    }
    public EntryPane(String label, k curval) {
        this(label, curval, Constants.DEFAULT_LABEL_WIDTH);
    }
    
    public EntryPane(String label, k curval, Double labelWidth) {
        String txt = "";
        if (curval != null) {
            txt = curval.toString();
        }
        entryName = new Label(label);
        entryName.setPrefWidth(labelWidth);
        entryName.setAlignment(Pos.CENTER_RIGHT);
        getChildren().add(entryName);
        entryField = new TextField(txt);

        UnaryOperator<TextFormatter.Change> myFilter = change -> {
            String newText = change.getControlNewText();
            if (isValidEntry(newText)) {
                return change; // Accept the change
            } else {
                return null; // Reject the change
            }
        };

        entryField.setTextFormatter(new TextFormatter<>(myFilter));
        entryField.setPrefColumnCount(10);
        getChildren().add(entryField);
        setSpacing(5.0);
    }

    protected boolean isValidEntry(String in) {
        return true;
    }
    
    protected boolean isValidDecimalPoint(String in) {
        boolean retval = false;
        int indx = in.indexOf(".");
        if (indx > 0) {
            retval = (in.indexOf(".", indx + 1) < 0);
        }

        return retval;
    }

    public TextField getEntryField() {
        return entryField;
    }

    public Label getEntryName() {
        return entryName;
    }
    
    protected Integer toInteger() {
        if (StringUtils.isNotEmpty(entryField.getText())) {
            return Integer.valueOf(entryField.getText());
        } else {
            return null;
        }
    }
    
    protected Double toDouble() {
        if (StringUtils.isNotEmpty(entryField.getText())) {
            return Double.valueOf(entryField.getText());
        } else {
            return null;
        }
    }
    
    public void setText(Object o) {
        if (o != null) {
            entryField.setText(o.toString());
        } else {
            entryField.setText("");
        }
    }

}
