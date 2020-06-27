package com.lyming.threadcoreknowledge.threadobjectclasscommonmethods;

/**
 * description:
 * ID从1开始，JVM运行起来后，我们自己创建的线程的ID早已不是2
 * @author lyming
 * @date 2020/6/27 9:51 下午
 */
public class Id {
    public static void main(String[] args) {
        Thread thread = new Thread();
        System.out.println("主线程的ID"+Thread.currentThread().getId());
        System.out.println("子线程的ID"+thread.getId());
    }
}
