package com.lyming.threadcoreknowledge.stopthreads;

/**
 * description:
 *  run无法抛出checked Exception，只能用try/catch
 * @author lyming
 * @date 2020/6/24 3:32 下午
 */
public class RunThrowException {

    public void aVoid() throws Exception {
        throw new Exception();
    }

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run()  {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
