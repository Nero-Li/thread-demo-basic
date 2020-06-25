package com.lyming.threadcoreknowledge.createthreads;

/**
 * description:
 * 用Thread方式实现线程
 * @author lyming
 * @date 2020/6/23 4:02 下午
 */
public class ThreadStyle extends Thread{
    @Override
    public void run() {
        System.out.println("用Thread类实现线程");
    }

    public static void main(String[] args) {
        new ThreadStyle().start();
    }
}
