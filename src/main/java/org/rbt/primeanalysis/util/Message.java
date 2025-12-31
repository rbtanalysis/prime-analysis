package org.rbt.primeanalysis.util;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 *
 * @author rbtuc
 */
public class Message extends BorderPane {
    public Message(String text) {
        Label l = new Label(text);
        l.setFont(new Font(25));
        this.setCenter(l);
     }
}
