package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
public class PartitionsChart extends HBox {

    private final PrimeAnalysis app;
    private final Map<BigDecimal, PrimePartition> partitionMap;

    public PartitionsChart(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        this.app = app;
        this.partitionMap = partitionMap;
        TabPane tp = new TabPane();
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.setSide(Side.BOTTOM);

        for (MinMaxHolder range : app.getConfig().getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            tab.setContent(buildPartitionsScatterChart(partitionMap, getChartTitle(partitionMap.size()), range.getMin(), range.getMax()));
            tp.getTabs().add(tab);
        }

        addContextMenu(tp);

        getChildren().add(new ScrollPane(tp));
    }

    private XYChart buildPartitionsScatterChart(Map<BigDecimal, PrimePartition> pmap, String title, BigDecimal startRadians, BigDecimal endRadians) {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setAutoRanging(false);
        //      yAxis.setAutoRanging(false);

        XYChart<Number, Number> retval = new LineChart<Number, Number>(xAxis, yAxis);
        retval.setPrefWidth(app.getConfig().getChartWidth() - (Constants.DEFAULT_CHART_WIDTH_REDUCTION * app.getConfig().getChartWidth()));
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

                XYChart.Data data = new XYChart.Data(rads.doubleValue(), crCnt);
                series.getData().add(data);

                Tooltip tt = new Tooltip(pp.getToolTipText());

                retval.getData().add(series);
                Tooltip.install(data.getNode(), tt);
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

    protected void addContextMenu(TabPane tp) {
        final ContextMenu contextMenu = getChartContextMenu(tp);

        tp.getSelectionModel().selectFirst();
        tp.setOnContextMenuRequested(e -> {
            contextMenu.show(tp, e.getScreenX(), e.getScreenY());
            e.consume(); // Prevents default OS context menu from appearing
        });

    }

    private ContextMenu getChartContextMenu(TabPane tabPane) {
        MenuItem print = new MenuItem("Print");
        MenuItem exit = new MenuItem("Exit");

        print.setOnAction((ActionEvent e) -> {
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(app.getStage())) {
                double scaleX = pageLayout.getPrintableWidth() / tabPane.getBoundsInLocal().getWidth();
                double scaleY = pageLayout.getPrintableHeight() / tabPane.getBoundsInLocal().getHeight();
                Transform scale = new Scale(scaleX * app.getConfig().getPrintScaleFactor(), scaleY * app.getConfig().getPrintScaleFactor());
                tabPane.getTransforms().add(scale);
                boolean success = job.printPage(tabPane);
                if (success) {
                    job.endJob(); // commit the print job
                }

                tabPane.getTransforms().remove(scale);
            }
        });

        exit.setOnAction((ActionEvent e) -> {
            System.exit(0);
        });

        // 3. Create a ContextMenu and add MenuItems to it
        ContextMenu retval = new ContextMenu();

        retval.getItems().addAll(print, new SeparatorMenuItem(), exit);
        return retval;
    }

    protected boolean isDesiredData(BigDecimal startRadians, BigDecimal endRadians, BigDecimal currads, Integer gap) {
        return ((currads.compareTo(startRadians) > 0)
                && (currads.compareTo(endRadians) < 0)
                && app.getConfig().getSelectedGaps().contains(gap));
    }

    protected String getChartTitle(Integer numPartitions) {
        DecimalFormat df = new DecimalFormat("##,###,###");
        return "Prime Counts by Radian\npartition count="
                + df.format(numPartitions) + " prime count="
                + df.format(app.getPrimes().size())
                + " decimal scale="
                + app.getConfig().getBigDecimalScale().getScale();

    }

    protected XYChart.Series getSeries(String name) {
        XYChart.Series retval = new XYChart.Series();
        retval.setName(name);
        return retval;
    }

    protected String getCountLabel() {
        if (app.getConfig().isUseLogForCounts()) {
            return "ln(count)";
        } else {
            return "count";
        }
    }

}
