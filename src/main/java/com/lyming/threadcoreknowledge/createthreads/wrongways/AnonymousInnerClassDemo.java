package com.lyming.threadcoreknowledge.createthreads.wrongways;

/**
 * description:
 *  匿名内部类的方式
 * @author lyming
 * @date 2020/6/23 4:25 下午
 */
public class AnonymousInnerClassDemo {
    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        }.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        }).start();
    }
}
