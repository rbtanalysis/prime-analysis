/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.rbt.primeanalysis.util;

import java.math.BigDecimal;

/**
 *
 * @author rbtuc
 */
public class MinMaxHolder {
    private BigDecimal min = BigDecimal.valueOf(Double.MAX_VALUE);
    private BigDecimal max = BigDecimal.valueOf(Double.MIN_VALUE);

    public MinMaxHolder() {
        
    }
    
    public MinMaxHolder(String min, String max) {
        this.min = new BigDecimal(min);
        this.max = new BigDecimal(max);
    }
    
    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }
    
    
}
