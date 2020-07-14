package com.lyming.singleton;

/**
 * @Description :双重检查（推荐面试使用）
 * @Author : Lyming
 * @Date: 2020-07-14 23:14
 */
public class Singleton6 {
    //用volatile防止重排序造成NPE,同时保证可见性
    private volatile static Singleton6 instance;

    private Singleton6() {

    }

    public static Singleton6 getInstance() {
        if (instance == null) {
            synchronized (Singleton6.class) {
                if (instance == null) {
                    instance = new Singleton6();
                }
            }
        }
        return instance;
    }
}
