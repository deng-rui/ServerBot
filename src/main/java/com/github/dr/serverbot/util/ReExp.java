package com.github.dr.serverbot.util;

import com.github.dr.serverbot.util.log.Log;

public abstract class ReExp {

    private int retryfreq = 0;

    // 重试的睡眠时间
    private int sleepTime = 0;

    public ReExp setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public ReExp setRetryFreq(int retryfreq) {
        this.retryfreq = retryfreq;
        return this;
    }

    /**
     * 重试
     * @return
     */
    protected abstract Object runs() throws Exception;

    public Object execute() {
        for (int i = 0; i < retryfreq; i++) {
            try {
                return runs();
            } catch (Exception e) {
                Log.error("", e);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        return null;
    }


    //public Object submit(ExecutorService executorService) {return executorService.submit((Callable) () -> execute());}

}