package com.haier.interconn.hcloud.trace;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-24  18:09
 */
public class NamedThreadFactory implements ThreadFactory {


    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    private final boolean mDaemo;



    NamedThreadFactory(String namePrefix,boolean daemoon){

        this.namePrefix = namePrefix;

        this.mDaemo = daemoon;

        //why？？
        SecurityManager s = System.getSecurityManager();
        group = ( s == null ) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }




    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     * create a thread is rejected
     */
    @Override
    public Thread newThread(Runnable r) {
        String name = namePrefix + poolNumber;
        Thread ret = new Thread(group,r,name,0);
        ret.setDaemon(mDaemo);
        return ret;
    }
}
