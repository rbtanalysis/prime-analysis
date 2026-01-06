package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.StringConverter;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Constants;

/**
 *
 * @author rbtuc
 */
public class RadianConvergenceChart extends BaseChart {

    public RadianConvergenceChart(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        super(app, partitionMap);
      //  FlowPane fp = new FlowPane();
        setTop(getChartTitle("Radian Growth", partitionMap.size()));
        TabPane tp = new TabPane();

        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.setSide(Side.BOTTOM);

        Tab tab = new Tab();
        XYChart chart = buildRadianConvergenceChart(partitionMap);
        chart.setPrefSize(Constants.DEFAULT_CHART_WIDTH, Constants.DEFAULT_CHART_HEIGHT);

        tab.setContent(chart);

        app.getUtil().makeDraggable(chart);

        tp.getTabs().add(tab);

        addContextMenu(tp);

        SingleSelectionModel<Tab> selectionModel = tp.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> obs, Tab oldTab, Tab newTab) {
                Platform.runLater(() -> {
                    getZoomHandler().clear((XYChart) oldTab.getContent());
                    getZoomHandler().clear((XYChart) newTab.getContent());
                });
            }
        });

        setCenter(new ScrollPane(tp));

        setPadding(new Insets(2, 10, 10, 10));
    }

    private XYChart buildRadianConvergenceChart(Map<BigDecimal, PrimePartition> pmap) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        XYChart<Number, Number> retval = new LineChart(xAxis, yAxis);
        retval.setPrefWidth(getConfig().getChartWidth());
        retval.setLegendVisible(false);
      //  xAxis.setAutoRanging(false);
     //   yAxis.setAutoRanging(false);

        List<PrimePartition> partitions = new ArrayList(pmap.values());
        Collections.sort(partitions);
        XYChart.Series series = getSeries("");
        int cnt = 0;
        for (PrimePartition pp : partitions) {
            if (isDesiredData(pp.getGap())) {
                XYChart.Data data = new XYChart.Data(cnt, pp.getRadian().doubleValue());
                series.getData().add(data);
                cnt++;
           }
        }

        retval.getData().add(series);

        for (Object o : series.getData()) {
            XYChart.Data data = (XYChart.Data) o;
            setTooltip(data.getNode(), "radian: " + data.getXValue());
        }

        series.getNode().setStyle(getCustomStyle());

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            DecimalFormat decimalFormat = new DecimalFormat("#0.######");

            @Override
            public String toString(Number object) {
                // Format the number using the DecimalFormat
                return decimalFormat.format(object);
            }

            @Override
            public Number fromString(String string) {
                // Not needed for simple display formatting
                return null;
            }
        });

    //    xAxis.setLowerBound(0);
      //  xAxis.setUpperBound(partitions.size());
        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10.0);
        xAxis.setLabel("index");
       
        double lowerBound = partitions.get(0).getRadian().doubleValue();
        yAxis.setLowerBound(lowerBound);
        double upperBound = partitions.get(partitions.size() - 1).getRadian().doubleValue();
        yAxis.setUpperBound(upperBound + (0.01 * upperBound));
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10.0);
        yAxis.setLabel("radian");
        
         retval.setOnScroll(event -> {
            double zoomFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;
            getZoomHandler().zoom(retval, zoomFactor, event.getSceneX(), event.getSceneY());
            event.consume();

        });

        return retval;
    }

    private String getCustomStyle() {
        StringBuilder retval = new StringBuilder();
        retval.append("-fx-opacity: 0.5; ");
        retval.append("-fx-stroke-width: 1.75;");
        return retval.toString();
    }
}
