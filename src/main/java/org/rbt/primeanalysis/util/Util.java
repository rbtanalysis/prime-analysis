package org.rbt.primeanalysis.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import javafx.scene.Node;
import org.rbt.primeanalysis.PrimeAnalysis;

/**
 *
 * @author rbtuc
 */
public class Util {

    private final DecimalFormat SCI_FORMAT = new DecimalFormat("0.########E0");

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

    public BigDecimal toBigDecimal(Double in) {
        return toBigDecimal(in.toString());
    }

    public void makeDraggable(Node node) {
        node.setOnMousePressed(event -> {
            // Record the initial mouse position relative to the node's current translation
            mouseAnchorX = event.getSceneX() - node.getTranslateX();
            mouseAnchorY = event.getSceneY() - node.getTranslateY();
            if (node.getUserData() == null) {
                node.setUserData(new Double[]{event.getSceneX(), event.getSceneY()});
            }
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            // Update the node's translation based on the new mouse position and initial anchor
            node.setTranslateX(event.getSceneX() - mouseAnchorX);
            node.setTranslateY(event.getSceneY() - mouseAnchorY);
            event.consume();
        });
    }

    public String toScientific(BigDecimal in) {
        return SCI_FORMAT.format(in);
    }

    public Double getTorusArea(Double prime, Double pprime) {
        Double gap = prime - pprime;
        Double r = gap / 2.0;
        Double R = prime - r;
 
        Double area = 4.0 * Math.pow(Math.PI, 2) * R * r;
        
        return area;
    }

    public Double getRingArea(Double prime, Double pprime) {
        Double area = Math.PI * (Math.pow(prime, 2.0) - Math.pow(pprime, 2.0));
        return area;
    }

}
