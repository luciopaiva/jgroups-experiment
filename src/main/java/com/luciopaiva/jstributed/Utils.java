package com.luciopaiva.jstributed;

import java.util.Collection;

class Utils {

    /**
     * Compare left with right and populate onlyLeft and onlyRight accordingly.
     */
    static <T> void distinctInCollections(Collection<T> left, Collection<T> right,
                                          Collection<T> onlyLeft, Collection<T> onlyRight) {
        onlyLeft.clear();
        onlyRight.clear();
        left.forEach(item -> {
            if (!right.contains(item)) {
                onlyLeft.add(item);
            }
        });
        right.forEach(item -> {
            if (!left.contains(item)) {
                onlyRight.add(item);
            }
        });
    }
}
