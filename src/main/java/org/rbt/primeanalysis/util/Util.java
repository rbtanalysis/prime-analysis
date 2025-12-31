/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.rbt.primeanalysis.util;

import java.math.BigDecimal;
import org.rbt.primeanalysis.PrimeAnalysis;

/**
 *
 * @author rbtuc
 */
public class Util {
    private final PrimeAnalysis app;
    
    public Util(PrimeAnalysis app) {
        this.app = app;
    }
    public BigDecimal toBigDecimal(String in) {
        Config cfg = app.getConfig();
        return new BigDecimal(in).setScale(cfg.getBigDecimalScale().getScale(), cfg.getBigDecimalScale().getRoundingMode());
    }

    public BigDecimal toBigDecimal(Long in) {
        return toBigDecimal(in.toString());
    }

    public BigDecimal toBigDecimal(Double in) {
        return toBigDecimal(in.toString());
    }

    public BigDecimal toBigDecimal(BigDecimal in) {
        return toBigDecimal(in.toString());
    }

    public BigDecimal twoPi() {
        return toBigDecimal(Math.PI * 2.0);
    }

    public BigDecimal piSquared() {
        return toBigDecimal(Math.pow(Math.PI, 2.0));
    }

}
