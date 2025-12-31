/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.rbt.primeanalysis.util;

import org.rbt.primeanalysis.util.BigDecimalScale;

/**
 *
 * @author rbtuc
 */
public class Constants {

    // prime text file location
    public static final String DEFAULT_PRIME_FILES_DIR = "/dev/projects/primeanalysis/src/main/primefiles/";

    // javafx chart annotation css
    public static final String DEFAULT_CHART_ANNOTATION_CSS = "-fx-border-color: transparent; "
            + "-fx-font-size: 10px; "
            + "-fx-background-color: transparent; "
            + "-fx-border-width: 0; "
            + "-fx-padding: 0; "
            + "-fx-fill: darkred;";

    public static final Integer DEFAULT_CHART_WIDTH = 1400;
    public static final Integer DEFAULT_CHART_HEIGHT = 800;

    // 1 to 50 (contains 1 million primes each) - set to any value 1 to 50
    public static final Integer DEFAULT_PRIME_FILE_LOAD_CNT = 50;

    // scale and rounding settings for BigDecimal math - pass in desired
    // scale for big decimal precision
    public static final BigDecimalScale DEFAULT_BD_SCALE = new BigDecimalScale(6);
    public static final BigDecimalScale DEFAULT_BD_HIGH_PREC_SCALE = new BigDecimalScale(9);

    // need to reduce chart scale for proper printing
    public static final Double DEFAULT_PRINT_SCALE_FACTOR = 0.8;

    // array of min,max big decimals to define scale for
    // chart display 
    public static final String[][] DEFAULT_PARTITION_RANGES = {
        {"1.539000", "1.571400"},
        {"1.566500", "1.570550"},
        {"1.569705", "1.570470"},
        {"1.569750", "1.570440"},
        {"1.570220", "1.570550"},
        {"1.570440", "1.571000"}};

    
}
