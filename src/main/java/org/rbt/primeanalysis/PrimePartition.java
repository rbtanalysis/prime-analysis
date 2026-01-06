package org.rbt.primeanalysis;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author rbtuc
 */
public class PrimePartition implements Serializable, Comparable<PrimePartition> {
    public static final String CSV_HEADER = "radian,count";
    private BigDecimal radian = BigDecimal.ZERO;
    private Long count = 0l;
    private Integer index;
    private final PrimeAnalysis app;
    private Set <Integer> gaps = new TreeSet();

    public PrimePartition(PrimeAnalysis app, BigDecimal radian) {
        this.radian = radian;
        this.app = app;
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

    public String getToolTipText() {
        StringBuilder retval = new StringBuilder();

        retval.append("radian: ");
        retval.append(getRadian());
        retval.append("\ncount: ");
        retval.append(getCount());
        retval.append("\n");
        retval.append("gaps: ");
        retval.append(getGapList());

        return retval.toString();
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
    
    public void addGap(Integer gap) {
        gaps.add(gap);
    }
    
    public String getGapList() {
        StringBuilder retval = new StringBuilder();
        String comma = "";
        
        for (Integer gap : gaps) {
            retval.append(comma);
            retval.append(gap);
            comma = ",";
        }
        
        return retval.toString();
    }
}
