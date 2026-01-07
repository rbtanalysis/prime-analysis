package org.rbt.primeanalysis.ui.control;

/**
 *
 * @author rbtuc
 */
public class NumericEntryPane<k> extends EntryPane {
    public NumericEntryPane(String label, k curval) {
        super(label, curval);
    }
    
    public NumericEntryPane(String label, k curval, Double labelWidth) {
        super(label, curval, labelWidth);
    }

    @Override
    public boolean isValidEntry(String in) {
        return in.matches ("\\d*") || isValidDecimalPoint(in) || in.trim().startsWith("-");
    }
}
