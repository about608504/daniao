package com.mygdx.game;

import java.util.concurrent.Callable;

/**
 * @Author: Liu
 * @Date: 2018/6/1 16:28
 */
public class TimeCounter implements Callable<Boolean> {

    private long times;

    public TimeCounter(long times) {
        this.times = times;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Boolean call() throws Exception {
        System.out.println("exchange call");
        Thread.sleep(times);
        return true;
    }
}
