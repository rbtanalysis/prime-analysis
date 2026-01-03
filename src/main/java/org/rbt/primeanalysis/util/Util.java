package org.rbt.primeanalysis.util;

import java.math.BigDecimal;
import javafx.scene.Node;
import org.rbt.primeanalysis.PrimeAnalysis;

/**
 *
 * @author rbtuc
 */
public class Util {

    private final PrimeAnalysis app;
    private double mouseAnchorX;
    private double mouseAnchorY;

    public Util(PrimeAnalysis app) {
        this.app = app;
    }

    public BigDecimal toBigDecimal(String in) {
        Config cfg = app.getConfig();
        return new BigDecimal(in).setScale(cfg.getBigDecimalScale().getScale(), cfg.getBigDecimalScale().getRoundingMode());
    }

    public BigDecimal toBigDecimal(Long in) {
        return toBigDecimal(in.toString());
    }

    public BigDecimal toBigDecimal(Double in) {
        return toBigDecimal(in.toString());
    }

    public BigDecimal toBigDecimal(BigDecimal in) {
        return toBigDecimal(in.toString());
    }

    public BigDecimal twoPi() {
        return toBigDecimal(Math.PI * 2.0);
    }

    public BigDecimal piSquared() {
        return toBigDecimal(Math.pow(Math.PI, 2.0));
    }

    public void makeDraggable(Node node) {
        node.setOnMousePressed(event -> {
            // Record the initial mouse position relative to the node's current translation
            mouseAnchorX = event.getSceneX() - node.getTranslateX();
            mouseAnchorY = event.getSceneY() - node.getTranslateY();
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            // Update the node's translation based on the new mouse position and initial anchor
            node.setTranslateX(event.getSceneX() - mouseAnchorX);
            node.setTranslateY(event.getSceneY() - mouseAnchorY);
            event.consume();
        });
    }
  }
