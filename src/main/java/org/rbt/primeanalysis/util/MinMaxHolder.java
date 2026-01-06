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
    private Double min = Double.valueOf(Double.MAX_VALUE);
    private Double max = Double.valueOf(Double.MIN_VALUE);

    public MinMaxHolder() {
        
    }
    
    public MinMaxHolder(String min, String max) {
        this.min = Double.valueOf(min);
        this.max = Double.valueOf(max);
    }
    
    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
    
    
    public void setMin(String min) {
        this.min = Double.valueOf(min);
    }

    public void setMax(String max) {
        this.max = Double.valueOf(max);
    }

    
}
