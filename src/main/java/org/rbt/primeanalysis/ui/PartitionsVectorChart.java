package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.StringConverter;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.CountDecreaseComparator;
import org.rbt.primeanalysis.util.MinMaxHolder;
import org.rbt.primeanalysis.util.RadianDecreaseComparator;

/**
 *
 * @author rbtuc
 */
public class PartitionsVectorChart extends BaseChartTab {

    public PartitionsVectorChart(PrimeAnalysis app, String tabName, Map<BigDecimal, PrimePartition> partitionMap) {
        super(app, tabName, partitionMap);
        TabPane tp = initTabPane(Side.BOTTOM);
        for (MinMaxHolder range : getConfig().getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            tab.setContent(buildRadianPartitionsVectorChart(partitionMap, getChartTitle(getPartitionMap().size()), range.getMin(), range.getMax()));

            tp.getTabs().add(tab);
            addContextMenu(tp);

        }
    }

    private XYChart buildRadianPartitionsVectorChart(Map<BigDecimal, PrimePartition> partitionMap, String title, BigDecimal startRadians, BigDecimal endRadians) {
        List<PrimePartition> pTemp = new ArrayList(partitionMap.values());
        List<PrimePartition> partitions = new ArrayList();
        for (PrimePartition pp : pTemp) {
            if (isDesiredData(startRadians, endRadians, pp.getRadian(), pp.getGap())) {
                partitions.add(pp);
            }
        }

        Collections.sort(partitions, new CountDecreaseComparator());

        Set<PrimePartition> top5 = new HashSet(partitions.subList(0, Math.min(partitions.size(), 5)));

        Collections.sort(partitions, new RadianDecreaseComparator());

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        //   xAxis.setAutoRanging(false);
        //   yAxis.setAutoRanging(false);
        yAxis.setLabel("");
        xAxis.setLabel("");
        LineChart<Number, Number> retval = new LineChart<Number, Number>(xAxis, yAxis);

        retval.setLegendVisible(false);
        retval.setCreateSymbols(false);
        retval.setTitle(title);

        MinMaxHolder mmCount = getMinMaxCount(startRadians, endRadians, partitions);
        List<Node> anodes = new ArrayList();
        for (PrimePartition pp : partitions) {
            XYChart.Series series = getSeries(retval);

            series.getData().add(new XYChart.Data(0, 0));
            BigDecimal count = pp.getCount();
            BigDecimal scale = count.divide(mmCount.getMax(), getConfig().getBigDecimalScale().getScale(), getConfig().getBigDecimalScale().getRoundingMode());
            Double x = scale.doubleValue() * Math.cos(pp.getRadian().doubleValue());
            Double y = scale.doubleValue() * Math.sin(pp.getRadian().doubleValue());
            XYChart.Data dataPoint = new XYChart.Data(x, y);
            if (top5.contains(pp)) {
                Node n = getAnnotationLabel(pp.getRadian(), count);
                anodes.add(n);
                dataPoint.setNode(n);
            }

            series.getData().add(dataPoint);
            retval.getData().add(series);
        }

        for (Node n : anodes) {
            n.toFront();
        }

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number radians) {
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number radians) {
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 4.0);
        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 4.0);

        return retval;
    }

}
