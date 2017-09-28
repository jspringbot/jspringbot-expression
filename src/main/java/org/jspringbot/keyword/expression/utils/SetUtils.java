package org.jspringbot.keyword.expression.utils;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {

    public static Set create() {
        return new HashSet();
    }

    public static boolean add(Set set, Object item) {
        return set.add(item);
    }
}
