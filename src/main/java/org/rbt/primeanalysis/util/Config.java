package org.rbt.primeanalysis.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rbtuc
 */
public class Config {
    private List<MinMaxHolder> ranges = new ArrayList();
    private String primeFilesDir = Constants.DEFAULT_PRIME_FILES_DIR;
    private Double printScaleFactor = Constants.DEFAULT_PRINT_SCALE_FACTOR;
    private Integer chartWidth = Constants.DEFAULT_CHART_WIDTH;
    private Integer chartHeight = Constants.DEFAULT_CHART_HEIGHT;
    private Integer primeFileLoadCount = Constants.DEFAULT_PRIME_FILE_LOAD_CNT;
    private BigDecimalScale bigDecimalScale = Constants.DEFAULT_BD_SCALE;
    private Boolean useLog = Boolean.FALSE;

    public String getPrimeFilesDir() {
        return primeFilesDir;
    }

    public void setPrimeFilesDir(String primeFilesDir) {
        this.primeFilesDir = primeFilesDir;
    }

    public Double getPrintScaleFactor() {
        return printScaleFactor;
    }

    public void setPrintScaleFactor(Double printScaleFactor) {
        this.printScaleFactor = printScaleFactor;
    }

    public Integer getChartWidth() {
        return chartWidth;
    }

    public void setChartWidth(Integer chartWidth) {
        this.chartWidth = chartWidth;
    }

    public Integer getChartHeight() {
        return chartHeight;
    }

    public void setChartHeight(Integer chartHeight) {
        this.chartHeight = chartHeight;
    }

    public Integer getPrimeFileLoadCount() {
        return primeFileLoadCount;
    }

    public void setPrimeFileLoadCount(Integer primeFileLoadCount) {
        this.primeFileLoadCount = primeFileLoadCount;
    }

    public List<MinMaxHolder> getRanges() {
        if (ranges.isEmpty()) {
            for (String[] mm : Constants.DEFAULT_PARTITION_RANGES) {
                ranges.add(new MinMaxHolder(mm[0], mm[1]));
            }
        }
        return ranges;
    }

    public void setRanges(List<MinMaxHolder> radianRanges) {
        this.ranges = radianRanges;
    }

    public Config clone() {
        Config retval = new Config();

        retval.setBigDecimalScale(bigDecimalScale);
        retval.setPrimeFileLoadCount(primeFileLoadCount);
        retval.setRanges(getRanges());
        retval.setUseLog(useLog);

        return retval;
    }

    public Boolean isUseLog() {
        return useLog;
    }

    public void setUseLog(Boolean useLog) {
        this.useLog = useLog;
    }


    public void update(Config in) {
        this.primeFileLoadCount = in.getPrimeFileLoadCount();
        this.bigDecimalScale = in.getBigDecimalScale();
        this.useLog = in.isUseLog();
        this.setRanges(in.getRanges());
    }

    public BigDecimalScale getBigDecimalScale() {
        return bigDecimalScale;
    }

    public void setBigDecimalScale(BigDecimalScale bigDecimalScale) {
        this.bigDecimalScale = bigDecimalScale;
    }

}
