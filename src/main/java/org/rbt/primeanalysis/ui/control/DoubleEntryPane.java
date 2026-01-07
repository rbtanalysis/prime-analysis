package org.rbt.primeanalysis.ui.control;

/**
 *
 * @author rbtuc
 */
public class DoubleEntryPane extends NumericEntryPane<Double> {
    public DoubleEntryPane(String label, Double curval) {
        super(label, curval);
    }
    
    public DoubleEntryPane(String label, Double curval, Double labelWidth) {
        super(label, curval, labelWidth);
    }
}
