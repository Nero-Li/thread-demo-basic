package com.lyming.singleton;

/**
 * @Description :懒汉式（线程安全）（不推荐）
 * @Author : Lyming
 * @Date: 2020-07-14 23:10
 */
public class Singleton4 {

    private static Singleton4 instance;

    private Singleton4() {

    }

    public synchronized static Singleton4 getInstance() {
        if (instance == null) {
            instance = new Singleton4();
        }
        return instance;
    }
}
