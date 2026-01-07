package org.rbt.primeanalysis.ui.tab;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import org.apache.commons.lang3.StringUtils;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.ZoomHandler;
import org.rbt.primeanalysis.util.Config;
import org.rbt.primeanalysis.util.Util;

/**
 *
 * @author rbtuc
 */
public class BaseChart  extends BorderPane {
    protected final double ZOOM_FACTOR = 1.1;
    private final PrimeAnalysis app;
    private final Map<String, PrimePartition> partitionMap;
    private final ZoomHandler zoomHandler = new ZoomHandler();
    private DecimalFormat radianFormat;
    private DecimalFormat countFormat;
    
    public BaseChart(PrimeAnalysis app, Map<String, PrimePartition> partitionMap) {
        this.app = app;
        this.partitionMap = partitionMap;
        radianFormat = new DecimalFormat("#0." + StringUtils.repeat('#', app.getConfig().getBigDecimalScale().getScale()));
        countFormat = new DecimalFormat("##,###,###,##0");

    }
 
    protected void addContextMenu(TabPane tp) {
        final ContextMenu contextMenu = getChartContextMenu(tp);

        tp.getSelectionModel().selectFirst();
        tp.setOnContextMenuRequested(e -> {
            contextMenu.show(tp, e.getScreenX(), e.getScreenY());
            e.consume(); // Prevents default OS context menu from appearing
        });

    }

    private ContextMenu getChartContextMenu(Node node) {
        MenuItem print = new MenuItem("Print Chart");
        MenuItem exit = new MenuItem("Exit Application");

        print.setOnAction((ActionEvent e) -> {
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(app.getStage())) {
                double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInLocal().getWidth();
                double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInLocal().getHeight();
                Transform scale = new Scale(scaleX * app.getConfig().getPrintScaleFactor(), scaleY * app.getConfig().getPrintScaleFactor());
                node.getTransforms().add(scale);
                boolean success = job.printPage(node);
                if (success) {
                    job.endJob(); // commit the print job
                }

                node.getTransforms().remove(scale);
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

        ContextMenu retval = new ContextMenu();
        retval.getItems().addAll(print, new SeparatorMenuItem(), exit);

        return retval;
    }

    protected boolean isDesiredData(Double startRadians, Double endRadians, BigDecimal currads) {
         return ((currads.doubleValue() >= startRadians)
                && (currads.doubleValue() <= endRadians));
    }

    protected FlowPane getChartTitle(String name, Integer numPartitions) {
        FlowPane retval = new FlowPane();

        DecimalFormat df = new DecimalFormat("##,###,###");

        StringBuilder s = new StringBuilder();
        s.append(name);
        s.append(": ");
        s.append("count=");
        s.append(df.format(numPartitions));
        s.append(" primes=");
        s.append(df.format(app.getPrimes().size()));
        s.append(" scale=");
        s.append(app.getConfig().getBigDecimalScale().getScale());

        retval.setAlignment(Pos.CENTER);
        retval.getChildren().add(new Label(s.toString()));
        return retval;
    }

    protected XYChart.Series getSeries(String name) {
        XYChart.Series retval = new XYChart.Series();
        retval.setName(name);
        return retval;
    }

    protected Config getConfig() {
        return app.getConfig();
    }
  
    protected Util getUtil() {
        return app.getUtil();
    }

    public PrimeAnalysis getApp() {
        return app;
    }

    public Map<String, PrimePartition> getPartitionMap() {
        return partitionMap;
    }

    public ZoomHandler getZoomHandler() {
        return zoomHandler;
    }
    
    protected void setTooltip(Node node, String text) {
        Tooltip tt = new Tooltip(text);
        Tooltip.install(node, tt);
    }

    public DecimalFormat getRadianFormat() {
        return radianFormat;
    }
    
    public DecimalFormat getCountFormat() {
        return countFormat;
    }
    
 }
