package org.rbt.primeanalysis.ui;

/**
 *
 * @author rbtuc
 */
public class IntegerEntryPane extends EntryPane<Integer> {
    public IntegerEntryPane(String label, Integer curval) {
        super(label, curval);
    }
    
    public IntegerEntryPane(String label, Integer curval, Double labelWidth) {
        super(label, curval, labelWidth);
    }

    @Override
    protected boolean isValidEntry(String in) {
        return in.matches ("\\d*") || in.trim().startsWith("-");
    }
}
