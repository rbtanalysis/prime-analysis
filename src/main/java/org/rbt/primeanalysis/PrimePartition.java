package org.rbt.primeanalysis;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author rbtuc
 */
public class PrimePartition implements Serializable, Comparable {

    public static final String CSV_HEADER = "Radian,Count";
    private BigDecimal radian = BigDecimal.ZERO;
    private BigDecimal count = BigDecimal.ZERO;
    private Integer gap = 0;
    private Integer index;
    private final PrimeAnalysis app;

    public PrimePartition(PrimeAnalysis app, BigDecimal radian, Integer gap) {
        this.radian = radian;
        this.app = app;
        this.gap = gap;
    }

    public BigDecimal getDegrees() {
        return app.getUtil().toBigDecimal(Math.toDegrees(radian.doubleValue()));
    }

    public BigDecimal getCount() {

        if (app.getConfig().isUseLogForCounts()) {
            return app.getUtil().toBigDecimal(Math.log(count.doubleValue()));
        } else {
            return count;
        }
    }

    public String toString() {
        StringBuilder retval = new StringBuilder();
        retval.append(radian);
        retval.append(",");
        retval.append(getCount());

        return retval.toString();
    }

    public void incrementCount() {
        count = count.add(BigDecimal.ONE);
    }

    public BigDecimal getRadian() {
        return radian;
    }

    public void setRadian(BigDecimal radian) {
        this.radian = radian;
    }

    @Override
    public int compareTo(Object o) {
        return radian.compareTo(((PrimePartition) o).getRadian());
    }

    @Override
    public boolean equals(Object obj) {
        return radian.equals(obj);
    }

    @Override
    public int hashCode() {
        return radian.hashCode();
    }

    public Integer getGap() {
        return gap;
    }

    public String getToolTipText() {
        StringBuilder retval = new StringBuilder();

        retval.append("radian: ");
        retval.append(getRadian());
        retval.append("\ngap: ");
        retval.append(getGap());
        retval.append("\ncount: ");
        retval.append(getCount());

        return retval.toString();
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
