package com.lyming.threadcoreknowledge.startthread;

/**
 * description:
 *  对比start和run两种启动线程的方式
 * @author lyming
 * @date 2020/6/23 4:49 下午
 */
public class StartAndRunMethod {
    public static void main(String[] args) {
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName());

        };
        runnable.run();

        new Thread(runnable).start();
    }
}
