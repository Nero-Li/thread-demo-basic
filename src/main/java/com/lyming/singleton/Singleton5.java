package com.lyming.singleton;

/**
 * @Description :懒汉式（线程不安全）（不推荐）
 * @Author : Lyming
 * @Date: 2020-07-14 23:11
 */
public class Singleton5 {

    private static Singleton5 instance;

    private Singleton5() {

    }

    public static Singleton5 getInstance() {
        if (instance == null) {
            synchronized (Singleton5.class) {
                instance = new Singleton5();
            }
        }
        return instance;
    }
}
