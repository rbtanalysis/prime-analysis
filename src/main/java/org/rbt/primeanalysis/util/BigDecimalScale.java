package org.rbt.primeanalysis.util;

import java.math.RoundingMode;

/**
 *
 * @author rbtuc
 */
public class BigDecimalScale {
    public static final Integer DEFAULT_BIG_DECIMAL_SCALE = 6;
    private Integer scale = DEFAULT_BIG_DECIMAL_SCALE;
    private RoundingMode roundingMode = RoundingMode.HALF_EVEN;       

    public BigDecimalScale() {
    }

    public BigDecimalScale(int scale) {
        this.scale = scale;
    }
    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }
    
    
    
}
