package org.rbt.primeanalysis.ui;

/**
 *
 * @author rbtuc
 */
public class NumericEntryPane extends EntryPane<Number> {
    public NumericEntryPane(String label, Number curval) {
        super(label, curval);
    }
    
    public NumericEntryPane(String label, Number curval, Double labelWidth) {
        super(label, curval, labelWidth);
    }

    @Override
    protected boolean isValidEntry(String in) {
        return in.matches ("\\d*") || isValidDecimalPoint(in) || in.trim().startsWith("-");
    }
}
