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
    private Long count = 0l;
    private Integer gap = 0;
    private Integer index;
    private final PrimeAnalysis app;

    public PrimePartition(PrimeAnalysis app, BigDecimal radian, Integer gap) {
        this.radian = radian;
        this.app = app;
        this.gap = gap;
    }

    public Double getCount() {
        return count.doubleValue();
    }

    public String toString() {
        StringBuilder retval = new StringBuilder();
        retval.append(radian);
        retval.append(",");
        retval.append(count);

        return retval.toString();
    }

    public void incrementCount() {
        count++;
    }

    public BigDecimal getRadian() {
        return radian;
    }

    public void setRadian(BigDecimal radian) {
        this.radian = radian;
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
