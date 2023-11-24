package com.nihil.auth.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class CollectUtil {
    public static boolean hasSame(Collection<?> c1, Collection<?> c2){
        Objects.requireNonNull(c1);
        Objects.requireNonNull(c2);
        Iterator<?> it = c1.iterator();
        while (it.hasNext()) {
            if (c2.contains(it.next())) {
                return true;
            }
        }
        return false;
    }
}
