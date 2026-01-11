package org.rbt.primeanalysis;

import org.rbt.primeanalysis.util.Config;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.rbt.primeanalysis.ui.control.DomainEntry;
import org.rbt.primeanalysis.ui.tab.PartitionsDataTable;
import org.rbt.primeanalysis.ui.tab.PartitionsChart;
import org.rbt.primeanalysis.ui.tab.RadianChangeChart;
import org.rbt.primeanalysis.util.Constants;
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
    private List<Long> primes = null;

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
        load(config, message, false);
    }

    public void load(Config config, String message, boolean clear) {
        this.config = config;

        if (primes == null) {
            stage.setScene(getChartScene(new Message(message))); //, Constants.DEFAULT_CHART_WIDTH, Constants.DEFAULT_CHART_HEIGHT ));
        } else {
            stage.setScene(getChartScene(new Message(message), stage.getScene().getWidth(), stage.getScene().getHeight()));
        }

        stage.show();

        Platform.runLater(() -> {
            if (mainTabs == null) {
                mainTabs = new TabPane();
                mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            }

            if (primes == null) {
                primes = this.loadPrimes();
            }

            Map<String, PrimePartition> partitionMap = getPartitions(primes);

            List<Tab> tabs = mainTabs.getTabs();

            if (clear) {
                tabs.clear();
            }

            if ((tabs == null) || tabs.isEmpty()) {
                Tab t = new Tab("Partitions");
                t.setContent(new PartitionsChart(this, partitionMap));
                mainTabs.getTabs().add(t);
                t = new Tab("Radian Change");
                t.setContent(new RadianChangeChart(this, partitionMap));
                mainTabs.getTabs().add(t);
                mainTabs.getTabs().add(new Tab("Partition Data"));
            }

            BorderPane bp = new BorderPane(new PartitionsDataTable(partitionMap));
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
                        l.add(PrimePartition.CSV_HEADER);

                        for (PrimePartition pp : partitions) {
                            l.add(pp.toString());
                        }

                        util.writeCsv(f.getPath(), l);
                    }
                }
            });

            bp.setTop(b);
            if (tabs != null) {
                tabs.get(2).setContent(bp);
            }

            mainTabs.getSelectionModel().selectFirst();

            bp = new BorderPane(mainTabs);
            bp.setBottom(new DomainEntry(this,
                    config.getLowerBound(),
                    config.getUpperBound(),
                    config.getBigDecimalScale().getScale()));
            stage.setScene(getChartScene(bp));
            stage.show();
        });

    }

    private Scene getChartScene(Pane pane) {
        Scene retval = new Scene(pane);
        retval.getStylesheets().add(getClass().getResource("/chartstyles.css").toExternalForm());
        return retval;
    }

    private Scene getChartScene(Pane pane, double width, double height) {
        Scene retval = new Scene(pane, width, height);
        retval.getStylesheets().add(getClass().getResource("/chartstyles.css").toExternalForm());
        return retval;
    }

    private boolean isDesiredRadian(Double radian) {
        boolean retval = true;
        if ((config.getLowerBound() != null) && (config.getUpperBound() != null)) {
            retval = ((radian >= config.getLowerBound()) && (radian <= config.getUpperBound()));
        }

        return retval;
    }

    protected Map<String, PrimePartition> getPartitions(List<Long> primes) {
        Map<String, PrimePartition> retval = new HashMap();
        Long pp = null;
        Long maxCount = Long.MIN_VALUE;
        for (Long prime : primes) {
            if (pp != null) {
                BigDecimal radian = getGeometricModel(prime.doubleValue(), pp.doubleValue());

                if (isDesiredRadian(radian.doubleValue())) {
                    PrimePartition partition = retval.get(radian.toString());

                    if (partition == null) {
                        partition = new PrimePartition(this, radian);
                    }

                    partition.incrementCount();

                    if (maxCount < partition.getCount()) {
                        maxCount = partition.getCount().longValue();
                    }

                    partition.addGap((int) (prime - pp));
                    retval.put(radian.toString(), partition);
                }
            }

            pp = prime;
        }

        List<PrimePartition> l = new ArrayList(retval.values());
        Collections.sort(l);

        int cnt = 0;
        for (PrimePartition par : l) {
            par.setIndex(++cnt);
        }

        if (config.getLowerBound() == null) {
            config.setLowerBound(l.get(0).getRadian().doubleValue());
            config.setUpperBound(l.get(l.size() - 1).getRadian().doubleValue());
        }
        
        System.out.println("==================================");
        System.out.println("scale: " + getScale());
        System.out.println("partitions: " + l.size());
        System.out.println("primes: " + primes.size());
        System.out.println("bounds: " + config.getLowerBound() + " to " + config.getUpperBound());
        System.out.println("==================================");

        return retval;
    }

    protected List<Long> loadPrimes() {
        List<Long> retval = new ArrayList();
        for (int i = 0; i < config.getPrimeFileLoadCount(); ++i) {
            retval.addAll(loadPrimeFile(i + 1));
        }

        return retval;
    }

    private BigDecimal getGeometricModel(Double prime, Double pprime) {
        BigDecimal p = util.toBigDecimal(prime);
        BigDecimal pp = util.toBigDecimal(pprime);
        BigDecimal coneArea  = Constants.PI.multiply(p.pow(2).subtract(pp.pow(2)));
        return arctan(coneArea);
      }

    private BigDecimal arctan(BigDecimal in) {
        BigDecimal retval = util.toBigDecimal(Math.atan(in.doubleValue())); 
        return retval.remainder(Constants.TWO_PI).setScale(getScale(), getRoundingMode());
    }
    
    private List<Long> loadPrimeFile(int indx) {
        List<Long> retval = new ArrayList();
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(config.getPrimeFilesDir() + "primes-" + indx + ".txt"));
            String line;
            while ((line = lnr.readLine()) != null) {
                if (StringUtils.isNotEmpty(line)) {
                    retval.add(Long.valueOf(line.trim()));
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

    public Config getConfig() {
        return config;
    }

    public Util getUtil() {
        return util;
    }

    public Stage getStage() {
        return stage;
    }

    public List<Long> getPrimes() {
        return primes;
    }

    public Integer getScale() {
        return config.getBigDecimalScale().getScale();
    }

    public RoundingMode getRoundingMode() {
        return config.getBigDecimalScale().getRoundingMode();
    }
}
