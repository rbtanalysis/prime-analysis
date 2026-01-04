package org.rbt.primeanalysis.util;

/**
 *
 * @author rbtuc
 */
public class Constants {

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
    public static final BigDecimalScale DEFAULT_BD_SCALE = new BigDecimalScale(7);

    // need to reduce chart scale for proper printing
    public static final Double DEFAULT_PRINT_SCALE_FACTOR = 0.8;

    // array of min,max big decimals to define scale for
    // chart display 
    public static final String[][] DEFAULT_PARTITION_RANGES = {
        {"1.5540", "1.5708"},
        {"1.5633", "1.5708"},
        {"1.5674", "1.5708"},
        {"1.5690", "1.5708"},
        {"1.5705", "1.5708"},
        {"1.570555", "1.5708"}};
    
 
}
