package com.lyming.singleton;

/**
 * @Description : 饿汉式（静态代码块）（可用）
 * @Author : Lyming
 * @Date: 2020-07-14 23:06
 */
public class Singleton2 {
    private final static Singleton2 INSTANCE;

    static {
        INSTANCE = new Singleton2();
    }

    private Singleton2() {
    }

    public static Singleton2 getInstance() {
        return INSTANCE;
    }
}
