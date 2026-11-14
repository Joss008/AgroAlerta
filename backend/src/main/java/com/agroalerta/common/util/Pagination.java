package com.agroalerta.common.util;

public final class Pagination {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private Pagination() {
    }

    public static int page(int page) {
        return Math.max(page, 0);
    }

    public static int size(int size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
