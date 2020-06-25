package com.lyming.threadcoreknowledge.startthread;

/**
 * description:
 *  演示不能两次调用start方法，否则会报错
 * @author lyming
 * @date 2020/6/23 5:01 下午
 */
public class CantStartTwice {
    public static void main(String[] args) {
        Thread thread = new Thread();
        thread.start();
        thread.start();
    }
}
