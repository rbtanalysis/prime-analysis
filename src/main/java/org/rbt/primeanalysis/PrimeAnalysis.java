package org.rbt.primeanalysis;

import org.rbt.primeanalysis.util.Constants;
import org.rbt.primeanalysis.util.RadianDecreaseComparator;
import org.rbt.primeanalysis.util.CountDecreaseComparator;
import org.rbt.primeanalysis.util.MinMaxHolder;
import org.rbt.primeanalysis.util.Config;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.rbt.primeanalysis.util.Message;
import org.rbt.primeanalysis.util.Util;

/**
 * JavaFX App
 */
public class PrimeAnalysis extends Application {

    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private TabPane contextTabPane = null;
    private Config config = new Config();
    private Util util;
    private Stage stage;
    private TabPane mainTabs = null;
    List<BigDecimal> primes = null;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        util = new Util(this);
        stage.setTitle("Prime Analysis");
        load(config, "Loading initial prime data...");

        //  convertPrimeFiles();
        //     System.exit(0);
    }

    protected void load(Config config, String message) {
        this.config = config;

        stage.setScene(getChartScene(new Message(message)));
        stage.show();
  
        if (mainTabs == null) {
            mainTabs = new TabPane();
            mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
//            stage.setScene(getChartScene(new BorderPane(mainTabs)));
        }

        if (primes == null) {
            primes = loadPrimes();
        }

        Platform.runLater(() -> {
            Map<BigDecimal, PrimePartition> partitionMap = getPartitions(primes);

            List<Tab> tabs = mainTabs.getTabs();

            if ((tabs == null) || tabs.isEmpty()) {
                mainTabs.getTabs().add(new Tab("Scatter"));
                mainTabs.getTabs().add(new Tab("Bar"));
                mainTabs.getTabs().add(new Tab("Vector"));
                mainTabs.getTabs().add(new Tab("Partition Data"));
                mainTabs.getTabs().add(new ConfigurationTab(this));
            }

            drawPartitionsScatterChart(stage, partitionMap, tabs.get(0));
            drawPartitionsBarChart(stage, partitionMap, tabs.get(1));
            drawRadianPartitionsVectorChart(stage, partitionMap, tabs.get(2));
            ScrollPane sp = new ScrollPane(getPartitionsDataTable(partitionMap));
            sp.setFitToHeight(true);
            sp.setFitToWidth(true);
            BorderPane bp = new BorderPane(sp);
            Button b = new Button("Export to CSV");
            b.setOnAction(new EventHandler() {
                public void handleEvent(ActionEvent e) {
                }

                @Override
                public void handle(Event t) {
                    FILE_CHOOSER.setTitle("Export File");
                    FILE_CHOOSER.setInitialFileName("partition-data");
                    FILE_CHOOSER.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"),
                            new ExtensionFilter("Text Files", "*.txt"));
                    File f = FILE_CHOOSER.showSaveDialog(stage);

                    if (f != null) {
                        List<PrimePartition> partitions = new ArrayList(partitionMap.values());
                        Collections.sort(partitions);
                        List<String> l = new ArrayList();
                        if (config.isUseLogForCounts()) {
                            l.add(PrimePartition.CSV_HEADER.replace("Count", "ln(Count)"));
                        } else {
                            l.add(PrimePartition.CSV_HEADER);
                        }
                        
                        if (config.isUseLogForArea()) {
                            l.add(PrimePartition.CSV_HEADER.replace("Area", "ln(Area)"));
                        } else {
                            l.add(PrimePartition.CSV_HEADER);
                        }


                        for (PrimePartition pp : partitions) {
                            l.add(pp.toString());
                        }

                        writeCsv(f.getPath(), l);
                    }
                }
            });

            bp.setTop(b);
            tabs.get(3).setContent(bp);
            mainTabs.getSelectionModel().selectFirst();

            stage.setScene(getChartScene(new BorderPane(mainTabs)));

        });

    }

    private Scene getChartScene(Pane pane) {
        Scene retval = new Scene(pane, config.getChartWidth(), config.getChartHeight());
        retval.getStylesheets().add(getClass().getResource("/chartstyles.css").toExternalForm());
        return retval;
    }

    private TableView getPartitionsDataTable(Map<BigDecimal, PrimePartition> partitionMap) {
        TableView<PrimePartition> retval = new TableView();

        TableColumn<PrimePartition, BigDecimal> indexCol = new TableColumn("#");
        TableColumn<PrimePartition, BigDecimal> radianCol = new TableColumn("Adjusted Radian");
        TableColumn<PrimePartition, BigDecimal> degreeCol = new TableColumn("Degrees");
        TableColumn<PrimePartition, BigDecimal> prevCol = new TableColumn("Previous Radian");
        
        TableColumn<PrimePartition, BigDecimal> areaCol = null;
        
          if (config.isUseLogForCounts()) {
            areaCol = new TableColumn("ln(Area)");
        } else {
            areaCol = new TableColumn("Area");
        }
        
        TableColumn<PrimePartition, BigDecimal> fullRadianCol = new TableColumn("Full Radian");
        TableColumn<PrimePartition, BigDecimal> radianDecCol = new TableColumn("Radian Decrease");

        TableColumn<PrimePartition, BigDecimal> countCol = null;
        if (config.isUseLogForCounts()) {
            countCol = new TableColumn("ln(Count)");
        } else {
            countCol = new TableColumn("Count");
        }

        retval.getColumns().addAll(indexCol, radianCol, degreeCol, prevCol, areaCol, fullRadianCol, radianDecCol, countCol);

        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        radianCol.setCellValueFactory(new PropertyValueFactory<>("radian"));
        degreeCol.setCellValueFactory(new PropertyValueFactory<>("degrees"));
        prevCol.setCellValueFactory(new PropertyValueFactory<>("previousRadian"));
        areaCol.setCellValueFactory(new PropertyValueFactory<>("originalTorusArea"));
        fullRadianCol.setCellValueFactory(new PropertyValueFactory<>("fullRadian"));
        radianDecCol.setCellValueFactory(new PropertyValueFactory<>("radianDecrease"));

        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        List<PrimePartition> partitions = new ArrayList(partitionMap.values());
        Collections.sort(partitions);

        int indx = 1;
        for (PrimePartition p : partitions) {
            p.setIndex(indx);
            indx++;
        }
        retval.getItems().addAll(partitions);
        return retval;
    }

    private String getCountLabel() {
        if (config.isUseLogForCounts()) {
            return "ln(count)";
        } else {
            return "count";
        }
    }

    private String getAreaLabel() {
        if (config.isUseLogForArea()) {
            return "ln(area)";
        } else {
            return "count";
        }
    }

    private TabPane getTabPane(Side side) {
        TabPane retval = new TabPane();
        retval.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        retval.setSide(side);
        return retval;
    }

    private void drawPartitionsScatterChart(Stage stage, Map<BigDecimal, PrimePartition> partitionMap, Tab chartTab) {
        String title = getChartTitle(partitionMap.size());

        TabPane tabPane = (TabPane) chartTab.getContent();

        if (tabPane == null) {
            tabPane = getTabPane(Side.BOTTOM);
            chartTab.setContent(tabPane);
        } else {
            tabPane.getTabs().clear();
        }

        for (MinMaxHolder range : config.getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            tab.setContent(buildPartitionsScatterChart(partitionMap, title, range.getMin(), range.getMax()));
            tabPane.getTabs().add(tab);
        }

        addContextMenu(stage, tabPane);

    }

    private void addContextMenu(Stage stage, TabPane tabPane) {
        final ContextMenu contextMenu = getContextMenu(stage);

        tabPane.getSelectionModel().selectFirst();
        tabPane.setOnContextMenuRequested(e -> {
            contextTabPane = tabPane;
            contextMenu.show(tabPane, e.getScreenX(), e.getScreenY());
            e.consume(); // Prevents default OS context menu from appearing
        });

    }

    private XYChart buildPartitionsScatterChart(Map<BigDecimal, PrimePartition> pmap, String title, BigDecimal startRadians, BigDecimal endRadians) {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setAutoRanging(false);
        //      yAxis.setAutoRanging(false);

        XYChart<Number, Number> retval = new ScatterChart<Number, Number>(xAxis, yAxis);
        retval.setPrefWidth(config.getChartWidth() - (Constants.DEFAULT_CHART_WIDTH_REDUCTION * config.getChartWidth()));

        retval.setTitle(title);
        XYChart.Series series = getSeries();

        int cnt = 0;

        List<PrimePartition> partitions = new ArrayList(pmap.values());
        Collections.sort(partitions);
        for (PrimePartition pp : partitions) {
            Double crCnt = pp.getCount().doubleValue();
            BigDecimal rads = pp.getRadian();
            if ((rads.compareTo(startRadians) > 0) && (rads.compareTo(endRadians) < 0)) {
                series.getData().add(new XYChart.Data(rads.doubleValue(), crCnt));
            }
            cnt++;
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
        retval.getData().add(series);

        return retval;
    }

    private void drawRadianPartitionsVectorChart(Stage stage, Map<BigDecimal, PrimePartition> partitionMap, Tab chartTab) {
        String title = getChartTitle(partitionMap.size());

        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);

        chartTab.setContent(tabPane);
        for (MinMaxHolder range : config.getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            tab.setContent(buildRadianPartitionsVectorChart(partitionMap, title, range.getMin(), range.getMax()));

            tabPane.getTabs().add(tab);
            addContextMenu(stage, tabPane);

        }
    }

    private XYChart buildRadianPartitionsVectorChart(Map<BigDecimal, PrimePartition> partitionMap, String title, BigDecimal startRadians, BigDecimal endRadians) {
        List<PrimePartition> pTemp = new ArrayList(partitionMap.values());
        List<PrimePartition> partitions = new ArrayList();
        for (PrimePartition pp : pTemp) {
            if ((pp.getRadian().compareTo(startRadians) > 0) && (pp.getRadian().compareTo(endRadians) < 0)) {
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

        MinMaxHolder mmCount = this.getMinMaxCount(startRadians, endRadians, partitions);
        List<Node> anodes = new ArrayList();
        for (PrimePartition pp : partitions) {
            XYChart.Series series = getSeries();

            series.getData().add(new XYChart.Data(0, 0));
            BigDecimal count = pp.getCount();
            BigDecimal scale = count.divide(mmCount.getMax(), config.getBigDecimalScale().getScale(), config.getBigDecimalScale().getRoundingMode());
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

    private Text getAnnotationLabel(BigDecimal radian, BigDecimal count) {
        Text retval = new Text(radian + "\n" + count);
        retval.setStyle(Constants.DEFAULT_CHART_ANNOTATION_CSS);
        return retval;

    }

    private String getChartTitle(Integer numPartitions) {
        DecimalFormat df = new DecimalFormat("##,###,###");
        return "Prime Counts by Radian - partition count=" 
                + df.format(numPartitions) + ", prime count=" 
                + df.format(primes.size() )
                + ", decimal scale=" 
                + config.getBigDecimalScale().getScale();

    }

    private void drawPartitionsBarChart(Stage stage, Map<BigDecimal, PrimePartition> partitionMap, Tab chartTab) {
        TabPane tabPane = (TabPane) chartTab.getContent();

        if (tabPane == null) {
            tabPane = getTabPane(Side.BOTTOM);
            chartTab.setContent(tabPane);
        } else {
            tabPane.getTabs().clear();
        }

        tabPane.setSide(Side.BOTTOM);

        for (MinMaxHolder range : config.getRanges()) {
            Tab tab = new Tab();
            tab.setText(range.getMin() + "-" + range.getMax());
            tab.setContent(buildPartitionsBarChart(partitionMap, getChartTitle(partitionMap.size()), range.getMin(), range.getMax()));
            tabPane.getTabs().add(tab);
        }

        addContextMenu(stage, tabPane);
    }

    private XYChart buildPartitionsBarChart(Map<BigDecimal, PrimePartition> pmap, String title, BigDecimal startRadians, BigDecimal endRadians) {
        List<BigDecimal> areas = new ArrayList(pmap.keySet());
        Collections.sort(areas);

        final NumberAxis yAxis = new NumberAxis();
        final CategoryAxis xAxis = new CategoryAxis();

        yAxis.setLabel(getCountLabel());
        yAxis.setAutoRanging(false);

        XYChart retval = new BarChart(xAxis, yAxis);
        retval.setPrefWidth(config.getChartWidth() - (Constants.DEFAULT_CHART_WIDTH_REDUCTION * config.getChartWidth()));

        retval.setTitle(title);
        XYChart.Series<String, Number> series = getSeries("");

        Long maxCount = Long.MIN_VALUE;
        for (BigDecimal rads : areas) {
            BigDecimal crCnt = pmap.get(rads).getCount();
            if ((rads.compareTo(startRadians) > 0) && (rads.compareTo(endRadians) < 0)) {
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

    XYChart.Series getSeries() {
        return getSeries("radians");
    }

    XYChart.Series getSeries(String name) {
        XYChart.Series retval = new XYChart.Series();
        retval.setName(name);
        return retval;
    }

    protected Map<BigDecimal, PrimePartition> getPartitions(List<BigDecimal> primes) {
        Map<BigDecimal, PrimePartition> retval = new HashMap();
        BigDecimal pp = null;
        BigDecimal maxCount = BigDecimal.valueOf(Long.MIN_VALUE);
        BigDecimal maxArea = util.toBigDecimal("-1.0");
        BigDecimal minArea = util.toBigDecimal("" + Double.MAX_VALUE);
        for (BigDecimal prime : primes) {
            if (pp != null) {
                BigDecimal ta = getTorusArea(prime, pp);

                if (ta.compareTo(maxArea) > 0) {
                    maxArea = ta;
                }

                if (ta.compareTo(minArea) < 0) {
                    minArea = ta;
                }

                BigDecimal radians = getAdjustedRadians(ta, prime);

                PrimePartition partition = retval.get(radians);

                if (partition == null) {
                    partition = new PrimePartition(this, ta, radians);
                }

                partition.incrementCount();

                if (maxCount.compareTo(partition.getCount()) < 0) {
                    maxCount = partition.getCount();
                }

                retval.put(radians, partition);

            }

            pp = prime;
        }

        List<PrimePartition> l = new ArrayList(retval.values());
        Collections.sort(l);

        for (int i = 1; i < l.size(); ++i) {
            PrimePartition p1 = l.get(i - 1);
            PrimePartition p2 = l.get(i);
            p2.setPreviousRadian(p1.getRadian());
        }

        System.out.println("**************************************");
        System.out.println("prime count: " + primes.size());
        System.out.println("partition count: " + retval.size());
        System.out.println("max prime: " + primes.get(primes.size() - 1).longValue());
        System.out.println("min area: " + minArea);
        System.out.println("max area: " + maxArea);
        System.out.println("maxCount: " + maxCount);
        System.out.println("bd scale: " + config.getBigDecimalScale().getScale());
        System.out.println("**************************************");

        return retval;
    }

    private void writeCsv(String name, List<String> lines) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(name);
            for (String line : lines) {
                pw.println(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            pw.close();
        }

    }

    protected List<BigDecimal> loadPrimes() {
        List<BigDecimal> retval = new ArrayList();
        for (int i = 0; i < config.getPrimeFileLoadCount(); ++i) {
            retval.addAll(loadPrimeFile(i + 1));
        }
        return retval;
    }

    private BigDecimal getAdjustedRadians(BigDecimal torusArea, BigDecimal prime) {
        BigDecimal tan = torusArea.divide(prime, config.getBigDecimalScale().getScale(), config.getBigDecimalScale().getRoundingMode());
        BigDecimal rads = util.toBigDecimal(Math.atan(tan.doubleValue()));
        return util.toBigDecimal(rads.remainder(util.twoPi()));
    }

    // 3 dim torus
    protected BigDecimal getTorusArea(BigDecimal p, BigDecimal prev) {
        BigDecimal gap = util.toBigDecimal(p.subtract(prev));
        BigDecimal r = gap.divide(util.toBigDecimal("2.0"), config.getBigDecimalScale().getScale(), config.getBigDecimalScale().getRoundingMode());
        BigDecimal R = util.toBigDecimal(p.subtract(r));

        return util.toBigDecimal(R.multiply(r).multiply(util.piSquared()).multiply(util.toBigDecimal("4.0")));
    }

    private List<BigDecimal> loadPrimeFile(int indx) {
        List<BigDecimal> retval = new ArrayList();
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(config.getPrimeFilesDir() + "primes-" + indx + ".txt"));
            String line;
            while ((line = lnr.readLine()) != null) {
                if (StringUtils.isNotEmpty(line)) {
                    retval.add(util.toBigDecimal(line.trim()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                lnr.close();
            } catch (Exception ex) {
            }
        }
        return retval;
    }

    private void convertPrimeFiles() {
        PrintWriter pw = null;
        LineNumberReader lnr = null;

        for (int i = 30; i < 50; ++i) {
            try {
                pw = new PrintWriter(config.getPrimeFilesDir() + "primes-" + (i + 1) + ".txt");
                lnr = new LineNumberReader(new FileReader(config.getPrimeFilesDir() + "/primes" + (i + 1) + ".txt"));
                String line;
                while ((line = lnr.readLine()) != null) {
                    if (StringUtils.isNotEmpty(line)) {
                        if (StringUtils.isNumericSpace(line)) {
                            StringTokenizer st = new StringTokenizer(line);

                            while (st.hasMoreTokens()) {
                                pw.println(st.nextToken());
                            }
                        }
                    }
                }

                pw.close();
                lnr.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    lnr.close();
                } catch (Exception ex) {
                }
                try {
                    pw.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private ContextMenu getContextMenu(final Stage stage) {
        MenuItem print = new MenuItem("Print");
        MenuItem exit = new MenuItem("Exit");

        print.setOnAction((ActionEvent e) -> {
            if (contextTabPane != null) {
                Node node = contextTabPane;
                Printer printer = Printer.getDefaultPrinter();
                PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);

                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null && job.showPrintDialog(stage)) {
                    double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInLocal().getWidth();
                    double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInLocal().getHeight();
                    Transform scale = new Scale(scaleX * config.getPrintScaleFactor(), scaleY * config.getPrintScaleFactor());
                    node.getTransforms().add(scale);
                    boolean success = job.printPage(node);
                    if (success) {
                        job.endJob(); // commit the print job
                    }

                    node.getTransforms().remove(scale);
                }
            }
        });
        exit.setOnAction(event -> {
            System.exit(0);
        });

        // 3. Create a ContextMenu and add MenuItems to it
        ContextMenu retval = new ContextMenu();
        retval.getItems().addAll(print, new SeparatorMenuItem(), exit);
        return retval;
    }

    private MinMaxHolder getMinMaxCount(BigDecimal start, BigDecimal end, List<PrimePartition> partitions) {
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

    public Config getConfig() {
        return config;
    }

    public Util getUtil() {
        return util;
    }

    public Stage getStage() {
        return stage;
    }
}
