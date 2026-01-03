package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
public class PartitionsChart extends BaseChartTab {

    public PartitionsChart(PrimeAnalysis app, String tabName, Map<BigDecimal, PrimePartition> partitionMap) {
        super(app, tabName, partitionMap);
        TabPane tp = initTabPane(Side.BOTTOM);
        
        for (MinMaxHolder range : app.getConfig().getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            tab.setContent(buildPartitionsScatterChart(partitionMap, getChartTitle(partitionMap.size()), range.getMin(), range.getMax()));
            tp.getTabs().add(tab);
        }

        addContextMenu(tp);
    }

    private XYChart buildPartitionsScatterChart(Map<BigDecimal, PrimePartition> pmap, String title, BigDecimal startRadians, BigDecimal endRadians) {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setAutoRanging(false);
        //      yAxis.setAutoRanging(false);

        XYChart<Number, Number> retval = new LineChart<Number, Number>(xAxis, yAxis);
        retval.setPrefWidth(getConfig().getChartWidth() - (Constants.DEFAULT_CHART_WIDTH_REDUCTION * getConfig().getChartWidth()));
        retval.setLegendVisible(false);
        retval.setTitle(title);

        List<PrimePartition> partitions = new ArrayList(pmap.values());
        Collections.sort(partitions);
        for (PrimePartition pp : partitions) {
            Double crCnt = pp.getCount().doubleValue();
            BigDecimal rads = pp.getRadian();
            if (isDesiredData(startRadians, endRadians, rads, pp.getGap())) {
                XYChart.Series series = getSeries("");
               
                series.setName("");
                series.getData().add(new XYChart.Data(rads.doubleValue(), 0));
                series.getData().add(new XYChart.Data(rads.doubleValue(), crCnt));
                retval.getData().add(series);
            }
        }

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

        xAxis.setLowerBound(startRadians.doubleValue());
        xAxis.setUpperBound(endRadians.doubleValue());

        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 20.0);

        yAxis.setLabel(getCountLabel());
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 20.0);
        return retval;
    }

}
