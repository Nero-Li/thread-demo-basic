package com.lyming.background;

import java.util.HashMap;
import java.util.Map;

/**
 * description:构造函数中新建线程,可以用工厂模式修复
 *
 * @author lyming
 * @date 2020/7/4 11:58 上午
 */
public class MultiThreadsError6 {
    private Map<String, String> states;

    public MultiThreadsError6() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                states = new HashMap<>();
                states.put("1", "周一");
                states.put("2", "周二");
                states.put("3", "周三");
                states.put("4", "周四");
            }
        }).start();
    }

    public Map<String, String> getStates() {
        return states;
    }

    public static void main(String[] args) throws InterruptedException {
        MultiThreadsError6 multiThreadsError6 = new MultiThreadsError6();
//        Thread.sleep(1000);
        System.out.println(multiThreadsError6.getStates().get("1"));
    }
}
