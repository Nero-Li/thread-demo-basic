package com.lyming.deadlock;

/**
 * @Description :演示哲学家就餐问题导致的死锁
 * @Author : Lyming
 * @Date: 2020-07-15 17:51
 */
public class DiningPhilosophers {
    /**
     * 哲学家类
     */
    public static class Philosopher implements Runnable {

        //左手筷子,也就是锁
        private Object leftChopstick;

        public Philosopher(Object leftChopstick, Object rightChopstick) {
            this.leftChopstick = leftChopstick;
            this.rightChopstick = rightChopstick;
        }
        //右手筷子,也就是锁
        private Object rightChopstick;

        @Override
        public void run() {
            try {
                while (true) {
                    /**
                     * 就餐顺序:思考=>拿左手边筷子=>拿右手边筷子=>吃=>放下右手边筷子=>放下左手边筷子
                     */
                    doAction("Thinking");
                    synchronized (leftChopstick) {
                        doAction("Picked up left chopstick");
                        synchronized (rightChopstick) {
                            doAction("Picked up right chopstick - eating");
                            doAction("Put down right chopstick");
                        }
                        doAction("Put down left chopstick");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void doAction(String action) throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " " + action);
            Thread.sleep((long) (Math.random() * 10));
        }
    }

    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[5];
        Object[] chopsticks = new Object[philosophers.length];
        for (int i = 0; i < chopsticks.length; i++) {
            chopsticks[i] = new Object();
        }
        for (int i = 0; i < philosophers.length; i++) {
            Object leftChopstick = chopsticks[i];
            Object rightChopstick = chopsticks[(i + 1) % chopsticks.length];
/*            if (i == philosophers.length - 1) {
                //改变一个哲学家拿餐具的顺序,解决死锁
                philosophers[i] = new Philosopher(rightChopstick, leftChopstick);
            } else {
                philosophers[i] = new Philosopher(leftChopstick, rightChopstick);
            }*/
            philosophers[i] = new Philosopher(leftChopstick, rightChopstick);
            new Thread(philosophers[i], "哲学家" + (i + 1) + "号").start();
        }
    }
}

