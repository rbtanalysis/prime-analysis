package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.StringConverter;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Constants;
import org.rbt.primeanalysis.util.MinMaxHolder;

/**
 *
 * @author rbtuc
 */
public class RadianConvergenceChart extends BaseChart {

    public RadianConvergenceChart(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        super(app, partitionMap);
        FlowPane fp = new FlowPane();
        setTop(getChartTitle("Radian Convergence", partitionMap.size()));
        TabPane tp = new TabPane();

        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.setSide(Side.BOTTOM);

        for (MinMaxHolder range : app.getConfig().getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            XYChart chart = buildRadianConvergenceChart(partitionMap, range.getMin(), range.getMax());
            chart.setPrefSize(Constants.DEFAULT_CHART_WIDTH, Constants.DEFAULT_CHART_HEIGHT);

            tab.setContent(chart);

            app.getUtil().makeDraggable(chart);

            tp.getTabs().add(tab);
        }

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

    private XYChart buildRadianConvergenceChart(Map<BigDecimal, PrimePartition> pmap, BigDecimal startRadians, BigDecimal endRadians) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        XYChart<Number, Number> retval = new LineChart<Number, Number>(xAxis, yAxis);
        retval.setPrefWidth(getConfig().getChartWidth());
        retval.setLegendVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        List<PrimePartition> partitions = new ArrayList(pmap.values());
        Collections.sort(partitions);
        XYChart.Series series = getSeries("radian gap");
        for (PrimePartition pp : partitions) {
            if (pp.getPreviousRadian().doubleValue() > 0.0) {
                BigDecimal rads = pp.getRadian();
                BigDecimal diff = pp.getRadian().subtract(pp.getPreviousRadian());
                if (isDesiredData(startRadians, endRadians, rads, pp.getGap())) {
                    XYChart.Data data = new XYChart.Data(rads.doubleValue(), diff.doubleValue());
                    series.getData().add(data);
                }
            }
        }

        retval.getData().add(series);
        series.getNode().setStyle("-fx-stroke: crimson; -fx-stroke-width: 1.5;");

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

        PrimePartition p1 = partitions.get(1);
        PrimePartition p2 = partitions.get(partitions.size() - 1);
        xAxis.setLowerBound(p1.getRadian().doubleValue());
        xAxis.setUpperBound(p2.getRadian().doubleValue());

        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10.0);

        yAxis.setLowerBound(0);
        yAxis.setUpperBound(p2.getRadian().subtract(p2.getPreviousRadian()).doubleValue());

        yAxis.setLabel("change");
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 5.0);

        retval.setOnScroll(event -> {
            double zoomFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;
            getZoomHandler().zoom(retval, zoomFactor, event.getSceneX(), event.getSceneY());
            event.consume();

        });

        return retval;
    }

    protected String getCountLabel() {
        if (getConfig().isUseLogForCounts()) {
            return "ln(count)";
        } else {
            return "count";
        }
    }

}
