package org.rbt.primeanalysis.util;

import java.util.Comparator;
import org.rbt.primeanalysis.PrimePartition;

/**
 *
 * @author rbtuc
 */
public class CountDecreaseComparator implements Comparator <PrimePartition> {

    @Override
    public int compare(PrimePartition o1, PrimePartition o2) {
        return o2.getCount().compareTo(o1.getCount());
    }
    
}
