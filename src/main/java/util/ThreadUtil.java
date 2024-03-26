package util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    private static final int CPU_CORE_NUM = Runtime.getRuntime().availableProcessors();

    public static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            CPU_CORE_NUM,
            CPU_CORE_NUM * 3,
            1,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100)
    );
}
