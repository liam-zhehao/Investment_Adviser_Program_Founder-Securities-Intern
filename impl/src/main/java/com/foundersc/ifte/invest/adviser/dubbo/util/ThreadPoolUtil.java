package com.foundersc.ifte.invest.adviser.dubbo.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 共用的线程池
 *
 * @author hsh
 * @date 2022/01/20 下午8:06
 */
public class ThreadPoolUtil {

    private static final int POOL_SIZE = 16;
    private static final ThreadPoolExecutor EXECUTOR;
    private static final ListeningExecutorService LISTENING_EXECUTOR;

    static {
        EXECUTOR = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE * 2, 1L, MINUTES, new ArrayBlockingQueue<>(POOL_SIZE),
                new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                destroyInstance();
            }
        });
        LISTENING_EXECUTOR = MoreExecutors.listeningDecorator(EXECUTOR);
    }

    private ThreadPoolUtil() {
    }

    public static ThreadPoolExecutor getInstance() {
        return EXECUTOR;
    }

    public static ListeningExecutorService getListenExecutor() {
        return LISTENING_EXECUTOR;
    }

    private static void destroyInstance() {
        if (EXECUTOR != null) {
            EXECUTOR.shutdownNow();
        }
    }
}
