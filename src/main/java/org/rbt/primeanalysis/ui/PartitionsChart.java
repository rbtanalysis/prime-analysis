package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Constants;

/**
 *
 * @author rbtuc
 */
public class PartitionsChart extends BaseChart {

    public PartitionsChart(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        super(app, partitionMap);
        setTop(getChartTitle("Prime Partitions", partitionMap.size()));
        XYChart chart = buildPartitionsChart(partitionMap);
        chart.setPrefSize(Constants.DEFAULT_CHART_WIDTH, Constants.DEFAULT_CHART_HEIGHT);
        app.getUtil().makeDraggable(chart);
        setCenter(new ScrollPane(chart));
        setPadding(new Insets(2, 10, 10, 10));
    }

    private XYChart buildPartitionsChart(Map<BigDecimal, PrimePartition> pmap) {
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

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            DecimalFormat decimalFormat = new DecimalFormat("#0." + StringUtils.repeat('#', getConfig().getBigDecimalScale().getScale()));

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
