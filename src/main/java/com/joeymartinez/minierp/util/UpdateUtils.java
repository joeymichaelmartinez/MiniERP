package com.joeymartinez.minierp.util;

import java.util.function.Consumer;

public class UpdateUtils {

    private UpdateUtils() {}

    public static void updateIfPresent(String value, Consumer<String> setter) {
        if (value != null && !value.isEmpty()) {
            setter.accept(value);
        }
    }

    public static <T> void updateIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
