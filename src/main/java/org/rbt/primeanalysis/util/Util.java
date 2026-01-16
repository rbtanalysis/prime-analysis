package org.rbt.primeanalysis.util;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;
import javafx.scene.Node;
import org.apache.commons.lang3.StringUtils;
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


    public BigDecimal getTorusArea(Double prime, Double pprime) {
        BigDecimal p = toBigDecimal(prime);
        BigDecimal pp = toBigDecimal(pprime);
        BigDecimal r = p.subtract(pp).divide(BigDecimal.TWO, app.getScale(), app.getRoundingMode());
        BigDecimal R = p;

        return toBigDecimal(4.0).multiply(Constants.PI_SQR).multiply(R).multiply(r).setScale(app.getScale(), app.getRoundingMode());
    }


    public void convertPrimeFiles() {
        PrintWriter pw = null;
        LineNumberReader lnr = null;

        for (int i = 30; i < 50; ++i) {
            try {
                pw = new PrintWriter(app.getConfig().getPrimeFilesDir() + "primes-" + (i + 1) + ".txt");
                lnr = new LineNumberReader(new FileReader(app.getConfig().getPrimeFilesDir() + "/primes" + (i + 1) + ".txt"));
                String line;
                while ((line = lnr.readLine()) != null) {
                    if (StringUtils.isNotEmpty(line)) {
                        if (StringUtils.isNumericSpace(line)) {
                            StringTokenizer st = new StringTokenizer(line);

                            while (st.hasMoreTokens()) {
                                pw.println(st.nextToken());
                            }
                        }
                    }
                }

                pw.close();
                lnr.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    lnr.close();
                } catch (Exception ex) {
                }
                try {
                    pw.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    public void writeCsv(String name, List<String> lines) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(name);
            for (String line : lines) {
                pw.println(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            pw.close();
        }

    }

}
