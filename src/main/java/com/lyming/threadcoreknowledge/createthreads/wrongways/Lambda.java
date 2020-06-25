package com.lyming.threadcoreknowledge.createthreads.wrongways;

/**
 * description:
 *  lambda表达式创建线程
 * @author lyming
 * @date 2020/6/23 4:24 下午
 */
public class Lambda {
    public static void main(String[] args) {
        new Thread(() -> System.out.println(Thread.currentThread().getName())).start();
    }
}
