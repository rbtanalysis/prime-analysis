package org.rbt.primeanalysis.util;

/**
 *
 * @author rbtuc
 */
public class Config {
    private String primeFilesDir = Constants.DEFAULT_PRIME_FILES_DIR;
    private Double printScaleFactor = Constants.DEFAULT_PRINT_SCALE_FACTOR;
    private Integer chartWidth = Constants.DEFAULT_CHART_WIDTH;
    private Integer chartHeight = Constants.DEFAULT_CHART_HEIGHT;
    private Integer primeFileLoadCount = Constants.DEFAULT_PRIME_FILE_LOAD_CNT;
    private BigDecimalScale bigDecimalScale = Constants.DEFAULT_BD_SCALE;

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


    public Config clone() {
        Config retval = new Config();

        retval.setBigDecimalScale(bigDecimalScale);
        retval.setPrimeFileLoadCount(primeFileLoadCount);

         return retval;
    }

    public BigDecimalScale getBigDecimalScale() {
        return bigDecimalScale;
    }

    public void setBigDecimalScale(BigDecimalScale bigDecimalScale) {
        this.bigDecimalScale = bigDecimalScale;
    }
}
