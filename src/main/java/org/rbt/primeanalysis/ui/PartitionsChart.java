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
import org.rbt.primeanalysis.util.MinMaxHolder;

/**
 *
 * @author rbtuc
 */
public class PartitionsChart extends BaseChart {
    public PartitionsChart(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        super(app, partitionMap);
        setTop(getChartTitle("Radian Partitions", partitionMap.size()));
        TabPane tp = new TabPane();

        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.setSide(Side.BOTTOM);

        for (MinMaxHolder range : app.getConfig().getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            XYChart chart = buildPartitionsChart(partitionMap, range.getMin(), range.getMax());
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

    private XYChart buildPartitionsChart(Map<BigDecimal, PrimePartition> pmap, Double startRadians, Double endRadians) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        XYChart<Number, Number> retval = new LineChart(xAxis, yAxis);
        retval.setPrefWidth(getConfig().getChartWidth());
        retval.setLegendVisible(false);

        List<PrimePartition> partitions = new ArrayList(pmap.values());
        Collections.sort(partitions);
        for (PrimePartition pp : partitions) {
            Double crCnt = pp.getCount();
            BigDecimal rads = pp.getRadian();
            if (isDesiredData(startRadians, endRadians, rads, pp.getGap())) {
                XYChart.Series series = getSeries("");

                series.setName("");
                XYChart.Data data1 = new XYChart.Data(rads.doubleValue(), 0);
                series.getData().add(data1);

                XYChart.Data data2 = new XYChart.Data(rads.doubleValue(), crCnt);
                series.getData().add(data2);
                retval.getData().add(series);

                setTooltip(data2.getNode(), pp.getToolTipText());
                data1.getNode().setStyle("-fx-padding: 0;");
            }
        }

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            DecimalFormat decimalFormat = new DecimalFormat("#0.############");

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
        
        xAxis.setLabel("radian");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(partitions.get(0).getRadian().doubleValue());
        xAxis.setUpperBound(partitions.get(partitions.size() - 1).getRadian().doubleValue());
        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10.0);
        
        yAxis.setLabel("count");

        retval.setOnScroll(event -> {
            double zoomFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;
            getZoomHandler().zoom(retval, zoomFactor, event.getSceneX(), event.getSceneY());
            event.consume();

        });

        return retval;
    }
}
