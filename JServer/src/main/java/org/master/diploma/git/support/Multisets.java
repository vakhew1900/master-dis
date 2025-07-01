package org.master.diploma.git.support;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public final class Multisets {

    private Multisets() {

    }

    public static  <T> Multiset<T> intersect(Multiset<T> first, Multiset<T> second) {

        Multiset<T> intersection = HashMultiset.create();


        for (var element : first.elementSet() ) {
            int firstCount = first.count(element);
            int secondCount = second.count(element);

            if (secondCount > 0) {
                intersection.add(element, Math.min(firstCount, secondCount));
            }
        }

        return intersection;
    }
}
