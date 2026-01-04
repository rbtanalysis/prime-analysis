package org.rbt.primeanalysis.ui;

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
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Config;
import org.rbt.primeanalysis.util.Util;

/**
 *
 * @author rbtuc
 */
public class BaseChart  extends BorderPane {
    protected final double ZOOM_FACTOR = 1.1;
    private final PrimeAnalysis app;
    private final Map<BigDecimal, PrimePartition> partitionMap;
    private final ZoomHandler zoomHandler = new ZoomHandler();
    
    public BaseChart(PrimeAnalysis app, Map<BigDecimal, PrimePartition> partitionMap) {
        this.app = app;
        this.partitionMap = partitionMap;
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

    protected boolean isDesiredData(Integer gap) {
        return app.getConfig().getSelectedGaps().contains(gap);
    }
    protected boolean isDesiredData(BigDecimal startRadians, BigDecimal endRadians, BigDecimal currads, Integer gap) {
        
        return ((currads.compareTo(startRadians) > 0)
                && (currads.compareTo(endRadians) < 0)
                && isDesiredData(gap));
    }

    protected FlowPane getChartTitle(String name, Integer numPartitions) {
        FlowPane retval = new FlowPane();

        DecimalFormat df = new DecimalFormat("##,###,###");

        StringBuilder s = new StringBuilder();
        s.append(name);
        s.append(": ");
        s.append("partition count=");
        s.append(df.format(numPartitions));
        s.append(" prime count=");
        s.append(df.format(app.getPrimes().size()));
        s.append(" decimal scale=");
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

    public Map<BigDecimal, PrimePartition> getPartitionMap() {
        return partitionMap;
    }

    public ZoomHandler getZoomHandler() {
        return zoomHandler;
    }
    
    
    protected String getCountLabel() {
        if (getConfig().isUseLogForCounts()) {
            return "ln(count)";
        } else {
            return "count";
        }
    }


}
