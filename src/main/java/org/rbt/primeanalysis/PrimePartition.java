package org.rbt.primeanalysis;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author rbtuc
 */
public class PrimePartition implements Serializable, Comparable<PrimePartition> {
    public static final String CSV_HEADER = "radian,count";
    private BigDecimal radian = BigDecimal.ZERO;
    private BigDecimal count = BigDecimal.ZERO;
    private Integer gap = 0;
    private Integer index;
    private final PrimeAnalysis app;

    public PrimePartition(PrimeAnalysis app, BigDecimal radian, Integer gap) {
        this.radian = app.getUtil().toBigDecimal(radian);
        this.app = app;
        this.gap = gap;
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
        this.radian = app.getUtil().toBigDecimal(radian);
    }

    @Override
    public int compareTo(PrimePartition pp) {
        return radian.compareTo(pp.getRadian());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PrimePartition) {
            return radian.equals(((PrimePartition)obj).getRadian());
        } else {
            return false;
        }
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
