package com.lyming.singleton;

/**
 * @Description : 懒汉式（线程不安全）
 * @Author : Lyming
 * @Date: 2020-07-14 23:06
 */
public class Singleton3 {
    private static Singleton3 instance;

    private Singleton3() {

    }

    public static Singleton3 getInstance() {
        if (instance == null) {
            instance = new Singleton3();
        }
        return instance;
    }
}
