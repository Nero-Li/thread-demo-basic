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
1. 方法概览![线程重要方法](src/main/resources/课程资料/技术图片/线程重要方法.png)
2. wait(),notify(),notifyAll()
    - 用法分三个阶段:
        - 阻塞阶段:线程调用了wait就进入阻塞阶段,调用wait的前提是获得了monitor锁,直到遇到以下四种情况之一,才可能被唤醒  
        ①另一个线程调用这个对象的notify()方法而且刚好被唤醒的是本线程  
        ②另一个线程调用这个对象的notifyAll()方法  
        ③过了wait(long timeout)规定的超时时间,如果传入0就是永久等待  
        ④线程自身调用了interrupt()中断
        - 唤醒阶段:notify()是唤醒随机的线程,JVM不参与选取,notifyAll()是唤醒全部等待的线程
        - 遇到中断:进入wait()的线程,如果遇到中断,会抛出interruptException,并释放monitor锁
3. 代码示例
    - 展示wait和notify的基本用法 1. 研究代码执行顺序 2. 证明wait释放锁[Wait.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/Wait.java)
    - notify, notifyAll。 start先执行不代表线程先启动。[WaitNotifyAll](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/WaitNotifyAll.java)
    - wait只释放当前的那把锁[WaitNotifyReleaseOwnMonitor.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/WaitNotifyReleaseOwnMonitor.java)
    - wait(),notify(),notifyAll()的特点性质
        - 属于Object类,用之前必须拥有monitor,另外notify()只能唤醒其中一个
        - 这些方法都是native final
        - 类似功能有Condition
    - 手写生产者消费者设计模式
        - 什么是生产者消费者模式![生产者模式1](src/main/resources/课程资料/技术图片/生产者模式1.png)![生产者模式2](src/main/resources/课程资料/技术图片/生产者模式2.png)
        - 用wait/notify来实现生产者消费者模式[ProducerConsumerModel.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/ProducerConsumerModel.java)
    - 两个线程交替打印0~100的奇偶数[用synchronized关键字实现](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/WaitNotifyPrintOddEvenSyn.java),[用wait和notify实现,效率更高](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/WaitNotifyPrintOddEveWait.java)
    - wait()和notify()必须要在同步代码块中,有锁来保护,以免切换不受控制,会让锁
    - wait()属于Object类,那调用Thread.wait会怎么样?线程不适合作为锁,因为它会自动调用notify()方法
    - sleep():不释放锁(包括Synchronized和lock),[不释放synchronized的monitor](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/SleepDontReleaseMonitor.java),[不释放lock](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/SleepDontReleaseLock.java)
        - sleep()也能相应中断,和wait()相似,抛出InterruptedException,并清除中断状态[SleepInterrupted.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/SleepInterrupted.java)
    - join():谁用,别的线程都要等他
        - 普通用法==>[Join.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/Join.java)
        - 遇到中断==>[JoinInterrupt.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/JoinInterrupt.java)
        - join期间,线程所在的状态时Waiting==>[JoinThreadState.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/JoinThreadState.java)
        - 看源码可以看到,join主要就是用了wait()方法,但是没有看到notify,这也和上面介绍wait()方法时,不推荐用线程来做锁,因为在底层有一个`ensure_join`方法,调用了notifyAll()方法,线程在执行完毕会自动调用
        - 等待代码==>[JoinPrinciple.java](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/JoinPrinciple.java)
    - yield():释放CPU时间片,但是状态依然是Runnable,因为不会释放锁,也不会阻塞
        - 定位:JVM不保证遵循,也就是说即使调用了它,马上又被cpu调用(一般是CPU资源充足)
### 六.线程的各个属性
1. 线程各个属性总览
    - ![线程各属性总结](src/main/resources/课程资料/技术图片/线程各属性总结.png)
    - ![线程各属性概览](src/main/resources/课程资料/技术图片/线程各属性概览.png)
2. 线程id
    - [ID从1开始，JVM运行起来后，我们自己创建的线程的ID早已不是2](src/main/java/com/lyming/threadcoreknowledge/threadobjectclasscommonmethods/Id.java)
    ```java
     /* For generating thread ID ,默认值是0*/
        private static long threadSeqNumber;
        /* ++在前面,所以id从1开始 */
        private static synchronized long nextThreadID() {
            return ++threadSeqNumber;
        }
    ```
    - 自己创建的线程为什么不是2?因为JVM在后台为了程序运行的准备工作还创建了很多线程
3. 线程名字
    - 默认线程名字的源码
    ```java
      public Thread() {
          init(null, null, "Thread-" + nextThreadNum(), 0);
      }
    ```
    - 修改线程的名字
    ```java
   /* Java thread status for tools,
    * initialized to indicate thread 'not yet started'
    */
   /* 线程还没有启动的时候 */
   private volatile int threadStatus = 0;
   
    public final synchronized void setName(String name) {
        checkAccess();
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        /* Thread有两个名字,一个是name,一个是NativeName,后者一旦线程
           启动就无法更改 */
        this.name = name;
        if (threadStatus != 0) {
            setNativeName(name);
        }
    }
    ``` 
4. 守护线程
    - 作用:给用户线程提供服务,JVM停止(一般是代码执行完毕),守护线程停止(发现用户线程都停止了,没守护对象了)
    - 线程类型默认继承自父线程,即守护线程创建的线程都是守护线程,用户线程创建的线程都是用户线程,有一个修改守护属性的方法叫`setDeamon()`
    - 通常而言,守护线程都是由JVM自动启动,而不是用户去启动,JVM启动的时候有一个非守护线程==>main()函数
    - 守护线程不影响JVM退出,JVM想退出只看还有没有用户线程运行
    - 守护线程和用户线程基本相同,主要区别就是是否影响JVM退出,用户线程一般是做业务逻辑的,守护线程是服务于用户线程的
    - 是否需要手动将用户线程设置为守护线程?==>不应该,有风险,如果该线程涉及到业务逻辑的处理,可能会因为JVM退出,处理中断
5. 线程优先级
    - 10个级别,默认5,而且子线程默认也是5
    ``` 
        /**
         * 最低的优先级.
         */
        public final static int MIN_PRIORITY = 1;
    
       /**
         * 默认的优先级
         */
        public final static int NORM_PRIORITY = 5;
    
        /**
         * 最大的优先级
         */
        public final static int MAX_PRIORITY = 10;
    ```
    - 程序设计不应该依赖优先级
        - 不同操作系统优先级不一样,优先级高度依赖操作系统,比如java有10个优先级,windows只有7个优先级,所以需要映射,所以优先级不可靠
        - 优先级会被操作系统改变,比如在windows中有个优先级推进器,会根据哪个线程努力给它分配资源,而不是靠优先级,而且如果设置的优先级过低,可能会造成饥饿
### 七.未捕获异常如何处理
- UncaughtExceptionHandler类,未捕获异常处理器
    - 为什么需要它?==>①主线程可以轻松发现异常,子线程却不行.[多线程，子线程发生异常](src/main/java/com/lyming/threadcoreknowledge/uncaughtexception/ExceptionInChildThread.java),因为主线程发生异常,程序停止,子线程异常,虽然可以打印出异常信息,但是很不容易发现②子线程异常无法用传统方法捕获,[线程的异常不能用传统方法捕获](src/main/java/com/lyming/threadcoreknowledge/uncaughtexception/CantCatchDirectly.java)
    - [自己的MyUncaughtExceptionHanlder](src/main/java/com/lyming/threadcoreknowledge/uncaughtexception/MyUncaughtExceptionHandler.java),[UseOwnUncaughtExceptionHandler.java](src/main/java/com/lyming/threadcoreknowledge/uncaughtexception/UseOwnUncaughtExceptionHandler.java)
    - 异常处理器的调用策略ThreadGroup.uncaughtException
    ``` 
        public void uncaughtException(Thread t, Throwable e) {
            //默认情况下parent=null
            if (parent != null) {
                parent.uncaughtException(t, e);
            } else {
                /* 调用Thread.setDefaultUncaughtExceptionHandler(...)
                方法设置的全局Handler进行处理 */
                Thread.UncaughtExceptionHandler ueh =
                    Thread.getDefaultUncaughtExceptionHandler();
                if (ueh != null) {
                    ueh.uncaughtException(t, e);
                } else if (!(e instanceof ThreadDeath)) {
                    //全局Handler也不存在就输出异常栈
                    System.err.print("Exception in thread \""
                                     + t.getName() + "\" ");
                    e.printStackTrace(System.err);
                }
            }
        }
    ```
### 八.双刃剑:多线程会导致的问题
## 常见面试问题