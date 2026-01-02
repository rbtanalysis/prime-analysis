package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Constants;

/**
 *
 * @author rbtuc
 */
public class PrimeSpiralChart extends Tab {

    private PrimeAnalysis app;
    private Map<BigDecimal, PrimePartition> partitionMap;

    public PrimeSpiralChart(PrimeAnalysis app, String tabName, Map<BigDecimal, PrimePartition> partitionMap) {
        super(tabName);
        this.app = app;
        this.partitionMap = partitionMap;
        setContent(buildChart());
    }

    private BorderPane buildChart() {
        BorderPane retval = new BorderPane();
        Canvas canvas = new Canvas(Constants.DEFAULT_CHART_WIDTH, Constants.DEFAULT_CHART_HEIGHT);
        

        // 2. Obtain the GraphicsContext to issue drawing commands
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 3. Set properties and draw shapes
        // Draw a filled blue rectangle
        gc.setFill(Color.BLUE);
        gc.fillRect(50, 50, 100, 70); // x, y, width, height

        // Draw a stroked red oval
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeOval(200, 150, 80, 50); // x, y, width, height

        retval.setCenter(new ScrollPane(canvas));
        
        return retval;

    }

}
