package org.rbt.primeanalysis.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author rbtuc
 */
public class Config {
    private List<BoundaryHolder> ranges = new ArrayList();
    private String primeFilesDir = Constants.DEFAULT_PRIME_FILES_DIR;
    private Double printScaleFactor = Constants.DEFAULT_PRINT_SCALE_FACTOR;
    private Integer chartWidth = Constants.DEFAULT_CHART_WIDTH;
    private Integer chartHeight = Constants.DEFAULT_CHART_HEIGHT;
    private Integer primeFileLoadCount = Constants.DEFAULT_PRIME_FILE_LOAD_CNT;
    private BigDecimalScale bigDecimalScale = Constants.DEFAULT_BD_SCALE;
    private Set<Integer> selectedGaps = new HashSet();

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

    public List<BoundaryHolder> getRanges() {
        if (ranges.isEmpty()) {
            for (String[] mm : Constants.DEFAULT_PARTITION_RANGES) {
                ranges.add(new BoundaryHolder(mm[0], mm[1]));
            }
        }
        return ranges;
    }

    public void setRanges(List<BoundaryHolder> radianRanges) {
        this.ranges = radianRanges;
    }

    public Config clone() {
        Config retval = new Config();

        retval.setBigDecimalScale(bigDecimalScale);
        retval.setPrimeFileLoadCount(primeFileLoadCount);
        retval.setRanges(getRanges());
        retval.setSelectedGaps(selectedGaps);

         return retval;
    }

    public BigDecimalScale getBigDecimalScale() {
        return bigDecimalScale;
    }

    public void setBigDecimalScale(BigDecimalScale bigDecimalScale) {
        this.bigDecimalScale = bigDecimalScale;
    }

    public Set<Integer> getSelectedGaps() {
        return selectedGaps;
    }

    public void setSelectedGaps(Set <Integer> selectedGaps) {
        this.selectedGaps = selectedGaps;
    }
}
