package org.rbt.primeanalysis;

import org.rbt.primeanalysis.ui.ConfigurationTab;
import org.rbt.primeanalysis.util.Config;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.rbt.primeanalysis.ui.PartitionsBarChart;
import org.rbt.primeanalysis.ui.PartitionsDataTable;
import org.rbt.primeanalysis.ui.PartitionsScatterChart;
import org.rbt.primeanalysis.ui.PartitionsVectorChart;
import org.rbt.primeanalysis.util.Message;
import org.rbt.primeanalysis.util.Util;

/**
 * JavaFX App
 */
public class PrimeAnalysis extends Application {

    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private Config config = new Config();
    private Util util;
    private Stage stage;
    private TabPane mainTabs = null;
    List<BigDecimal> primes = null;
    TreeSet<Integer> primeGapSet = null;

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

    public void load(Config config, String message) {
        this.config = config;

        stage.setScene(getChartScene(new Message(message)));
        stage.show();

        if (mainTabs == null) {
            mainTabs = new TabPane();
            mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        }

        if (primes == null) {
            primes = loadPrimes();
            primeGapSet =  loadPrimeGapSet(primes);

        }

        Platform.runLater(() -> {
            Map<BigDecimal, PrimePartition> partitionMap = getPartitions(primes);

            List<Tab> tabs = mainTabs.getTabs();

            if ((tabs == null) || tabs.isEmpty()) {
                mainTabs.getTabs().add(new PartitionsScatterChart(this, "Scatter", partitionMap));
                mainTabs.getTabs().add(new PartitionsBarChart(this, "Bar", partitionMap));
                mainTabs.getTabs().add(new PartitionsVectorChart(this, "Vector", partitionMap));
                mainTabs.getTabs().add(new Tab("Partition Data"));
                mainTabs.getTabs().add(new ConfigurationTab(this));
            }
            
            ScrollPane sp = new ScrollPane(new PartitionsDataTable(this, partitionMap));
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
            if (tabs != null) {
                tabs.get(3).setContent(bp);
            }
            
            mainTabs.getSelectionModel().selectFirst();

            stage.setScene(getChartScene(new BorderPane(mainTabs)));

        });

    }

    private Scene getChartScene(Pane pane) {
        Scene retval = new Scene(pane, config.getChartWidth(), config.getChartHeight());
        retval.getStylesheets().add(getClass().getResource("/chartstyles.css").toExternalForm());
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
                    Integer diff = Long.valueOf(prime.longValue() - pp.longValue()).intValue();
                    partition = new PrimePartition(this, ta, radians, diff);
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

    protected TreeSet<Integer> loadPrimeGapSet(List<BigDecimal> primes) {
        TreeSet<Integer> retval = new TreeSet();
        Long pp = null;
        for (BigDecimal prime : primes) {
            if (pp != null) {
                Long diff = prime.longValue() - pp;
                retval.add(diff.intValue());
                config.getSelectedGaps().add(diff.intValue());
            }
            pp = prime.longValue();
        }

        System.out.println("found " + retval.size() + " distinct gaps");
        
        return retval;

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

    public Config getConfig() {
        return config;
    }

    public Util getUtil() {
        return util;
    }

    public Stage getStage() {
        return stage;
    }

    public TreeSet<Integer> getPrimeGapSet() {
        return primeGapSet;
    }

    public List<BigDecimal> getPrimes() {
        return primes;
    }
  }
