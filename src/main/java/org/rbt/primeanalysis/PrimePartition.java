package org.rbt.primeanalysis;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author rbtuc
 */
public class PrimePartition implements Serializable, Comparable {
    public static final String CSV_HEADER = "Adjusted Radian,Degrees,Previous Adjusted Radian,UnAdjusted Radian,Torus Area,Prime Count,Radian Decrease";       
    private BigDecimal radian = BigDecimal.ZERO;
    private BigDecimal previousRadian = BigDecimal.ZERO;
    private BigDecimal count = BigDecimal.ZERO;
    private BigDecimal originalTorusArea = BigDecimal.ZERO;
    private Integer index = 0;
    private final PrimeAnalysis app;
    public PrimePartition(PrimeAnalysis app, BigDecimal originalTorusArea, BigDecimal radian) {
        this.radian = radian;
        this.app = app;
        this.originalTorusArea = originalTorusArea;
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
        retval.append(getDegrees());
        retval.append(",");
        retval.append(previousRadian);
        retval.append(",");
        retval.append(getFullRadian());
        retval.append(",");
        retval.append(originalTorusArea);
        retval.append(",");
        retval.append(getCount());
        
        retval.append(",");
        retval.append(getRadianDecrease());
                
        return retval.toString();
    }
    
    public void incrementCount() {
        count = count.add(BigDecimal.ONE);
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public BigDecimal getRadian() {
        return radian;
    }

    public void setRadian(BigDecimal radian) {
        this.radian = radian;
    }

    public BigDecimal getFullRadian() {
        return app.getUtil().toBigDecimal(originalTorusArea.divide(
                app.getUtil().twoPi(), 
                app.getConfig().getBigDecimalScale().getScale(), 
                app.getConfig().getBigDecimalScale().getRoundingMode()));
    }
    
    public BigDecimal getPreviousRadian() {
        return previousRadian;
    }
    
    public BigDecimal getRadianDecrease() {
        if (previousRadian != null) {
            return app.getUtil().toBigDecimal(radian.subtract(previousRadian));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public void setPreviousRadian(BigDecimal previousRadian) {
        this.previousRadian = previousRadian;
    }

    public BigDecimal getOriginalTorusArea() {
        if (app.getConfig().isUseLogForArea()) {
            return app.getUtil().toBigDecimal(Math.log(originalTorusArea.doubleValue()));
        } else {
            return originalTorusArea;
        }
    }

    public void setOriginalTorusArea(BigDecimal originalTorusArea) {
        this.originalTorusArea = originalTorusArea;
    }
    
    
    
    @Override
    public int compareTo(Object o) {
        return radian.compareTo(((PrimePartition)o).getRadian());
    }

    @Override
    public boolean equals(Object obj) {
        return radian.equals(obj); 
    }

    @Override
    public int hashCode() {
        return radian.hashCode();
    }

    
}
