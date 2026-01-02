package org.rbt.primeanalysis.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import org.rbt.primeanalysis.PrimeAnalysis;
import org.rbt.primeanalysis.PrimePartition;
import org.rbt.primeanalysis.util.Config;
import org.rbt.primeanalysis.util.Constants;
import org.rbt.primeanalysis.util.MinMaxHolder;

/**
 *
 * @author rbtuc
 */
public class BaseChartTab extends Tab {
    private PrimeAnalysis app;
    private Map<BigDecimal, PrimePartition> partitionMap;

    public BaseChartTab(PrimeAnalysis app, String tabName, Map<BigDecimal, PrimePartition> partitionMap) {
        super(tabName);
        this.app = app;
        this.partitionMap = partitionMap;
    }

    protected Text getAnnotationLabel(BigDecimal radian, BigDecimal count) {
        Text retval = new Text(radian + "\n" + count);
        retval.setStyle(Constants.DEFAULT_CHART_ANNOTATION_CSS);
        return retval;

    }

    protected String getAreaLabel() {
        if (app.getConfig().isUseLogForArea()) {
            return "ln(area)";
        } else {
            return "count";
        }
    }

    protected TabPane initTabPane(Side side) {
        TabPane retval = (TabPane)getContent();

        if (retval == null) {
            retval = new TabPane();
            retval.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            retval.setSide(side);
           setContent(retval);
        } else {
            retval.getTabs().clear();
        }

        return retval;
    }

    protected PrimeAnalysis getApp() {
        return app;
    }

    public Map<BigDecimal, PrimePartition> getPartitionMap() {
        return partitionMap;
    }

    protected XYChart.Series getSeries(XYChart chart) {
        return getSeries("radians");
    }

    protected XYChart.Series getSeries(String name) {
        XYChart.Series retval = new XYChart.Series();
        retval.setName(name);
        return retval;
    }

    protected Boolean isDesiredData(BigDecimal startRadians, BigDecimal endRadians, BigDecimal currads, Integer gap) {
        return ((currads.compareTo(startRadians) > 0) && (currads.compareTo(endRadians) < 0) && app.getConfig().getSelectedGaps().contains(gap));
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
    
    protected MinMaxHolder getMinMaxCount(BigDecimal start, BigDecimal end, List<PrimePartition> partitions) {
        MinMaxHolder retval = new MinMaxHolder();

        for (PrimePartition p : partitions) {
            if ((p.getRadian().compareTo(start) >= 0)
                    && (p.getRadian().compareTo(end) <= 0)) {
                if (p.getCount().compareTo(retval.getMax()) > 0) {
                    retval.setMax(p.getCount());
                }

                if (p.getCount().compareTo(retval.getMin()) < 0) {
                    retval.setMin(p.getCount());
                }
            }
        }

        return retval;
    }


    protected String getChartTitle(Integer numPartitions) {
        DecimalFormat df = new DecimalFormat("##,###,###");
        return "Prime Counts by Radian\npartition count="
                + df.format(numPartitions) + " prime count="
                + df.format(app.getPrimes().size())
                + " decimal scale="
                + app.getConfig().getBigDecimalScale().getScale();

    }

    protected String getCountLabel() {
        if (app.getConfig().isUseLogForCounts()) {
            return "ln(count)";
        } else {
            return "count";
        }
    }

    protected Config getConfig() {
        return app.getConfig();
    }
    
}
