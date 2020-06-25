## 线程8大核心基础
### 一.实现线程的方法到底有多少种
1. 实现多线程的官方文档中指出,实现多线程的方法有`两种`
    - 实现Runnable接口[RunnableStyle.java](src/main/java/com/lyming/threadcoreknowledge/createthreads/RunnableStyle.java)
    - 继承Thread类[ThreadStyle.java](src/main/java/com/lyming/threadcoreknowledge/createthreads/ThreadStyle.java)
    - 两种方法对比:实现Runnable方法更好(解耦,资源消耗,java不支持双继承)
2. 两种方法的本质对比
    - Runnable:最终调用target.run()
    - Thread:run()整个都被重写
    - 同时使用两种方法会怎么样[BothRunnableThread.java](src/main/java/com/lyming/threadcoreknowledge/createthreads/BothRunnableThread.java)  
    根据本质的对比可以知道,Thread的run被重写,传入Runnable对象也不会被调用,所以最后还是Thread重写的方法
3. 总结 
    - Oracle官方文档写出,创建线程的方法有两种
    - 准确的说,创建线程只有一种方法,那就是构造Thread类,而实现线程的执行单元有两种方式
        - 实现Runnable接口的run()方法,并把Runnable实例传给Thread类
        - 重写Thread的run()方法(继承Thread类)
### 二.怎么才是启动线程的正确方式(start)
1. start()和run()方法的比较[StartAndRunMethod.java](src/main/java/com/lyming/threadcoreknowledge/startthread/StartAndRunMethod.java)
2. start()方法原理解读
    - 启动新线程:只是告诉jvm可以调动,调用start方法的顺序,并不代表线程运行顺序
    - start方法做的准备工作:让线程处于就绪状态
    - 不能两次调用start方法,否则会抛出异常[CantStartTwice.java](src/main/java/com/lyming/threadcoreknowledge/startthread/CantStartTwice.java)原因是因为
    ```java
       //Thread.start()
        if (threadStatus != 0)//检查线程状态,0代表New
            throw new IllegalThreadStateException();
    ```  
    - 顺序为启动新线程检查线程状态==>加入线程组==>调用native方法start0(),见Thread.java的start()方法
3. run()方法的原理解读
    ```java
       public void run() {
           //target是一个Runnable类
           if (target != null) {
               target.run();
           }
       }
    ```
### 三.如何正确停止线程(难点)
1. 停止线程的原理
    - 使用interrupt通知,而不是强制,因为被通知停止的线程更清楚何时停止,从而避免强制停止可能带来的混乱状态
2. 最佳实践:如何正确停止线程
    1. 通常什么时候线程会停止
        - run()方法都执行完毕
        - 出现异常,方法中没有捕获
        - 停止之后,它所占用的资源,内存都会被jvm回收
    2. 正确的停止方法:interrupt()
        - 通常情况[RightWayStopThreadWithoutSleep.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/RightWayStopThreadWithoutSleep.java)
        - 线程可能被阻塞[RightWayStopThreadWithSleep.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/RightWayStopThreadWithSleep.java)  
        会抛出异常,一般sleep()或者阻塞住都会在catch中相应中断
        - 如果线程在每次迭代后都阻塞[RightWayStopThreadWithSleepEveryLoop.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/RightWayStopThreadWithSleepEveryLoop.java)  
        如果在执行过程中，每次循环都会调用sleep或wait等方法，那么不需要每次迭代都检查是否已中断
        - 如果while里面放try/catch，会导致中断失效[CantInterrupt.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/CantInterrupt.java)  
        原因是因为sleep()设计的理念是响应后(catch中处理了),interrupt标记位会被清除
    3. 实际开发中的两种最佳实践
        - 优先选择:传递中断[RightWayStopThreadInProd.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/RightWayStopThreadInProd.java)
        - 不想或无法传递:恢复中断
        - 不应该屏蔽中断
        - 响应中断的方法总结列表
            - Object.wait()/wait(long)/wait(long,int)
            - Thread.sleep(long)/sleep(long,int)
            - Thread.join()/join(long)/join(long,int)
            - java.util.concurrent.BlockingQueue.take()/put(E)
            - java.util.concurrent.locks.Lock.lockInterruptibly()
            - java.util.concurrent.CountDownLatch.await()
            - java.util.concurrent.CyclicBarrier.await()
            - java.util.concurrent.Exchanger.exchange(V)
            - java.nio.channels.InterruptibleChannel相关方法
            - java.nio.channels.Selector的相关方法
        - Java异常体系  
        ![Java异常体系](src/main/resources/课程资料/技术图片/Java异常体系.png)  
        RuntimeException是Unchecked Exception,和error一样,都是能通过编译,但是jvm不能提前预知,其他的都是Checked Exception,这样的异常,编译器要求手动处理,或者抛出去
3. 错误的停止方法
    1. 被弃用的stop(),suspend(),resume()方法
        - [StopThread.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/StopThread.java)会造成脏数据
        - suspend()会带着锁挂起,容易造成死锁
    2. 用volatile设置boolean标记位(重点,很有迷惑性,看上去可行)
        - 看似可行的情况[WrongWayVolatile.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/volatiledemo/WrongWayVolatile.java)
        - 局限的情况,陷入阻塞时，volatile是无法停止线程的 此例中，生产者的生产速度很快，消费者消费速度慢，所以阻塞队列满了以后，生产者会阻塞,等待消费者进一步消费[WrongWayVolatileCantStop.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/volatiledemo/WrongWayVolatileCantStop.java)
        - 用interrupt()修复[WrongWayVolatileFixed.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/volatiledemo/WrongWayVolatileFixed.java)
4. 停止线程相关的重要函数解析
    - static boolean interrupted():检测当前线程是否被中断,返回值后会把线程中断状态清除
    - boolean isInterrupted():检测当前线程是否被中断
    - Thread1.interrupted()的目的对象是当前调用它的线程,并不一定是Thread1[RightWayInterrupted.java](src/main/java/com/lyming/threadcoreknowledge/stopthreads/RightWayInterrupted.java)
### 四.线程的生命周期,6种状态
1. 有哪6种状态
    - ![线程的6个状态](src/main/resources/课程资料/技术图片/线程的6个状态.png)
    - New:已创建但是没启动(start方法)
    - Runnable:调用了start方法后,其实Runnable代表两种状态==>Ready和Running
    - Blocked:当线程进入被`Synchronize`(只有Synchronize修饰,其他的lock都不算)修饰的代码块,锁被其他线程拿走的时候
    - Waiting:[BlockedWaitingTimedWaiting.java](src/main/java/com/lyming/threadcoreknowledge/sixstates/BlockedWaitingTimedWaiting.java)
    - Timed Waiting:[BlockedWaitingTimedWaiting.java](src/main/java/com/lyming/threadcoreknowledge/sixstates/BlockedWaitingTimedWaiting.java)
    - Terminate:正常结束或者出现异常
2. 阻塞状态是什么
    - 一般而言,把Blocked,Waiting,Timed_Waiting都成为阻塞状态,不仅仅是Blocked
### 五.Thread和Object类中的重要方法详解
### 六.线程的各个属性
### 七.未捕获异常如何处理
### 八.双刃剑:多线程会导致的问题
## 常见面试问题