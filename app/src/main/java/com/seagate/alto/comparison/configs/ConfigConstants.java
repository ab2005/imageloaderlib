
package com.seagate.alto.comparison.configs;

public class ConfigConstants {
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    public static final int MAX_DISK_CACHE_SIZE = 80 * 1024 * 1024;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
}
