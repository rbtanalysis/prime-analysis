package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Constants;
import org.rbt.primeanalysis.util.MinMaxHolder;

/**
 *
 * @author rbtuc
 */
public class PartitionsBarChart extends BaseChartTab {
    public PartitionsBarChart(PrimeAnalysis app, String tabName, Map<BigDecimal, PrimePartition> partitionMap) {
        super(app, tabName, partitionMap);
        
        TabPane tp = initTabPane(Side.BOTTOM);

        
        for (MinMaxHolder range : getApp().getConfig().getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            tab.setContent(buildPartitionsBarChart(getPartitionMap(), getChartTitle(getPartitionMap().size()), range.getMin(), range.getMax()));
            tp.getTabs().add(tab);
        }

        addContextMenu(tp);
    }

    private XYChart buildPartitionsBarChart(Map<BigDecimal, PrimePartition> pmap, String title, BigDecimal startRadians, BigDecimal endRadians) {
        List<BigDecimal> areas = new ArrayList(pmap.keySet());
        Collections.sort(areas);

        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();

        yAxis.setLabel(getCountLabel());
        yAxis.setAutoRanging(false);

        XYChart retval = new BarChart(xAxis, yAxis);
        retval.setPrefWidth(getConfig().getChartWidth() - (Constants.DEFAULT_CHART_WIDTH_REDUCTION * getConfig().getChartWidth()));

        retval.setTitle(title);
        XYChart.Series<String, Number> series = getSeries("");

        Long maxCount = Long.MIN_VALUE;
        for (BigDecimal rads : areas) {
            PrimePartition pp = pmap.get(rads);
            BigDecimal crCnt = pp.getCount();
            if (isDesiredData(startRadians, endRadians, rads, pp.getGap())) {
                series.getData().add(new XYChart.Data(rads.toString(), crCnt.longValue()));
                if (crCnt.longValue() > maxCount) {
                    maxCount = crCnt.longValue();
                }
            }
        }

        retval.getData().add(series);

        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxCount);
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 5);
        return retval;
    }

    
}
