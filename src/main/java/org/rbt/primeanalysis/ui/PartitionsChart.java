package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
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
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
public class PartitionsChart extends BorderPane {

    final double ZOOM_FACTOR = 1.1;
    private double scaleValue = 1.0;

    private final PrimeAnalysis app;

    public PartitionsChart(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        this.app = app;

        setTop(getChartTitle(partitionMap.size()));
        TabPane tp = new TabPane();
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tp.setSide(Side.BOTTOM);

        for (MinMaxHolder range : app.getConfig().getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            ScrollPane sp = new ScrollPane(tab.getContent());
            sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
            sp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
            XYChart chart = buildPartitionsScatterChart(partitionMap, range.getMin(), range.getMax());
            chart.setPrefSize(Constants.DEFAULT_CHART_WIDTH, Constants.DEFAULT_CHART_HEIGHT);
            sp.setContent(chart);
            tab.setContent(sp);
            tp.getTabs().add(tab);
        }

        addContextMenu(tp);

        setCenter(tp);

        setPadding(new Insets(2, 10, 10, 10));
    }

    private XYChart buildPartitionsScatterChart(Map<BigDecimal, PrimePartition> pmap, BigDecimal startRadians, BigDecimal endRadians) {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setAutoRanging(false);
        //      yAxis.setAutoRanging(false);

        XYChart<Number, Number> retval = new LineChart<Number, Number>(xAxis, yAxis);
        retval.setPrefWidth(app.getConfig().getChartWidth());
        retval.setLegendVisible(false);

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

        xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10.0);

        yAxis.setLabel(getCountLabel());
        yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 5.0);

        retval.setOnScroll(event -> {
            event.consume(); // Prevent the event from bubbling up

            double zoomFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;

            // Calculate new scale value and clamp it
            double tmp = scaleValue * zoomFactor;

            if (tmp >= 1.0) {
                scaleValue = tmp;
                // Apply the new scale transformation
                retval.setScaleX(scaleValue);
                retval.setScaleY(scaleValue);

            }

        });

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
        MenuItem print = new MenuItem("Print Chart");
        MenuItem exit = new MenuItem("Exit Application");

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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(ButtonType.NO);
            alert.setHeaderText("Are you sure you want to exit?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                System.exit(0);
            }
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

    protected VBox getChartTitle(Integer numPartitions) {
        VBox retval = new VBox();

        DecimalFormat df = new DecimalFormat("##,###,###");
        retval.getChildren().add(new Label("Prime Counts by Radian"));
        retval.getChildren().add(new Label("partition count=" + df.format(numPartitions)));
        retval.getChildren().add(new Label("prime count=" + df.format(app.getPrimes().size())));
        retval.getChildren().add(new Label("decimal scale=" + app.getConfig().getBigDecimalScale().getScale()));

        return retval;
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
