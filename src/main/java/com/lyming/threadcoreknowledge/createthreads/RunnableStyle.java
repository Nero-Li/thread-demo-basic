package com.lyming.threadcoreknowledge.createthreads;

/**
 * description:
 * 用Runnable方式创建线程
 * @author lyming
 * @date 2020/6/23 4:01 下午
 */
public class RunnableStyle implements Runnable{
    public static void main(String[] args) {
        Thread thread = new Thread(new RunnableStyle());
        thread.start();
    }

    @Override
    public void run() {
        System.out.println("用Runnable方法实现线程");
    }
}
