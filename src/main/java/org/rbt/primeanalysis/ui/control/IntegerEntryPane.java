package org.rbt.primeanalysis.ui.control;

/**
 *
 * @author rbtuc
 */
public class IntegerEntryPane extends NumericEntryPane<Integer> {
    public IntegerEntryPane(String label, Integer curval) {
        super(label, curval);
    }
    
    public IntegerEntryPane(String label, Integer curval, Double labelWidth) {
        super(label, curval, labelWidth);
    }

    @Override
    public boolean isValidEntry(String in) {
        return in.matches ("\\d*") || in.trim().startsWith("-");
    }
}
