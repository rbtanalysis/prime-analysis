package org.rbt.primeanalysis.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author rbtuc
 */
public class Constants {
    public static final Integer DEFAULT_SCALE = 9;
    
    public static final BigDecimal PI = BigDecimal.valueOf(Math.PI).setScale(DEFAULT_SCALE, RoundingMode.HALF_EVEN);
    public static final BigDecimal TWO_PI = BigDecimal.valueOf(Math.PI * 2.0).setScale(DEFAULT_SCALE, RoundingMode.HALF_EVEN);
    public static final BigDecimal PI_SQR = BigDecimal.valueOf(Math.PI).pow(2).setScale(DEFAULT_SCALE, RoundingMode.HALF_EVEN);

    // prime text file location
    public static final String DEFAULT_PRIME_FILES_DIR = "/dev/projects/prime-analysis/src/main/primefiles/";

    // javafx chart annotation css
    public static final String DEFAULT_CHART_ANNOTATION_CSS = "-fx-border-color: transparent; "
            + "-fx-font-size: 10px; "
            + "-fx-background-color: transparent; "
            + "-fx-border-width: 0; "
            + "-fx-padding: 0; "
            + "-fx-fill: darkred;";
    
    public static final Integer DEFAULT_CHART_WIDTH = 1400;
    public static final Integer DEFAULT_CHART_HEIGHT = 700;

    // 1 to 50 (contains 1 million primes each) - set to any value 1 to 50
    public static final Integer DEFAULT_PRIME_FILE_LOAD_CNT = 50;

    // scale and rounding settings for BigDecimal math - pass in desired
    // scale for big decimal precision
    public static final BigDecimalScale DEFAULT_BD_SCALE = new BigDecimalScale(DEFAULT_SCALE);

    // need to reduce chart scale for proper printing
    public static final Double DEFAULT_PRINT_SCALE_FACTOR = 0.8;
    
    public static final Double DEFAULT_LABEL_WIDTH = 150.0;
    
}
