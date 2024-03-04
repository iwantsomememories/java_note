# 多线程

## 实现方式

1. 继承thread类：

   ```java
   public class MyThread extends Thread{
   
       @Override
       public void run(){
           for (int i = 0; i < 100; i++) {
               System.out.println(getName()+"Hello World");
           }
       }
   }
   
   public void demo1(){
       MyThread t1 = new MyThread();
       t1.setName("线程1");
       t1.start();
   }
   ```

2. 实现Runnable接口：

   ```java
   public class MyRun implements Runnable{
       @Override
       public void run() {
           for (int i = 0; i < 100; i++) {
               Thread t = Thread.currentThread();
               System.out.println(t.getName()+" HelloWorld!");
           }
       }
   }
   
   public void demo2(){
       MyRun mr = new MyRun();
       Thread t1 = new Thread(mr);
       t1.start();
   }
   ```

3. 利用Callable接口和Future接口实现：

   ```java
   public class MyCallable implements Callable<Integer> {
       @Override
       public Integer call() throws Exception {
           int sum = 0;
           for (int i = 0; i < 100; i++) {
               sum += i;
           }
           return sum;
       }
   }
   
   public void demo3() throws ExecutionException, InterruptedException {
       MyCallable mc = new MyCallable();
       FutureTask<Integer> ft = new FutureTask<>(mc);
       Thread t1 = new Thread(ft);
       t1.start();
       var i = ft.get();
       System.out.println(i);
   }
   ```

## 线程安全

### 同步

**同步代码块：**

```java
//锁对象，一定要是唯一的
static Object obj = new Object();

public void run() {
    while (true){
        synchronized (obj){
            if(ticket < 100){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ticket++;
                System.out.println(getName() + "正在卖第" + ticket + "张票");
            } else
                break;
        }
    }
}
```

**同步方法：**

```java
//非静态方法锁对象为this，静态方法锁对象为类字节码文件
private synchronized boolean extracted() {
    if(ticket == 100){
        return true;
    } else {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ticket++;
        System.out.println(Thread.currentThread().getName()+"正在卖第" + ticket + "张票");
    }
    return false;
}
```

**Lock锁：**

```java
public class threadSafe3 {
    public static void main(String[] args) {
        seller2 t1 = new seller2();
        seller2 t2 = new seller2();
        seller2 t3 = new seller2();

        t1.setName("窗口1");
        t2.setName("窗口2");
        t3.setName("窗口3");

        t1.start();
        t2.start();
        t3.start();
    }
}

class seller2 extends Thread{
    static int ticket = 0;
    static Lock lock = new ReentrantLock();
    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                if (ticket < 100) {
                    Thread.sleep(100);
                    ticket++;
                    System.out.println(getName() + "正在卖第" + ticket + "张票");
                } else
                    break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}
```

**实例——生产者与消费者：**

```java
public class waitAndNotify {
    public static void main(String[] args) {
        cook c = new cook();
        foodie f = new foodie();

        c.setName("厨师");
        f.setName("顾客");

        c.start();
        f.start();
    }
}

class cook extends Thread{

    @Override
    public void run() {
        while (true){
            synchronized (desk.lock){
                if(desk.count == 0)
                    break;
                else{
                    if(desk.food_flag == 1)
                        try {
                            desk.lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    else{
                        System.out.println("厨师做了一碗面条");
                        desk.food_flag = 1;
                        desk.lock.notifyAll();
                    }
                }
            }
        }
    }
}

class foodie extends Thread{
    /*
    1.循环
    2.同步代码块
    3.判断共享数据是否到了末尾（到了末尾）
    4.判断共享数据是否到了末尾（没到末尾，执行核心逻辑）
     */

    @Override
    public void run() {
        while(true){
            synchronized (desk.lock){
                if(desk.count == 0)
                    break;
                else {
                    if(desk.food_flag == 0){
                        try {
                            desk.lock.wait();//让当前线程跟锁进行绑定
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else{
                        desk.count--;
                        System.out.println("吃货在吃面条，还能再吃"+desk.count+"碗!");
                        desk.lock.notifyAll();//唤醒这把锁绑定的所有线程
                        desk.food_flag = 0;
                    }
                }
            }
        }
    }
}

class desk{
    /*
    作用：控制生产者和消费者的执行
     */

    //表示是否有食物
    public static int food_flag = 0;

    //消费者可消费总个数
    public static int count = 10;

    //锁对象
    public static Object lock = new Object();

}
```

### 阻塞队列

阻塞队列继承结构：Iterable-》Collection-》Queue-》BlockingQueue。

实现类：ArrayBlockingQueue(底层是数组、有界)、LinkedBlockingQueue（底层是链表，无界）

## 线程池

代码实现：

1. 创建线程池

   线程池的工具类通过调用方法返回不同类型的线程池对象

2. 提交任务

3. 所有的任务全部执行完毕，关闭线程池

```java
public static void main(String[] args) throws InterruptedException {
    //1.获取线程池对象
    ExecutorService pool1 = Executors.newCachedThreadPool();//无上限
    ExecutorService pool2 = Executors.newFixedThreadPool(3);//无上限
    

    //2.提交任务
    pool1.submit(new myRunable());
    Thread.sleep(1000);
    pool1.submit(new myRunable());
    Thread.sleep(1000);
    pool1.submit(new myRunable());
    Thread.sleep(1000);
    pool1.submit(new myRunable());
    Thread.sleep(1000);
    pool1.submit(new myRunable());

    //3.销毁线程池
    pool1.shutdown();
}
```

自定义线程池：

```java
ThreadPoolExecutor pool = new ThreadPoolExecutor(
        3,//核心线程数量，不能小于0
        6,//最大线程数，不能小于0,最大线程数>=核心线程数量
        60,//空闲线程最大存活时间
        TimeUnit.SECONDS,//时间单位
        new LinkedBlockingQueue<>(),//任务队列
        Executors.defaultThreadFactory(),//线程工厂
        new ThreadPoolExecutor.AbortPolicy()//拒绝策略
);
```

1. 核心线程满时，任务进入排队队列
2. 核心线程满且排队队列满时，创建临时线程执行任务
3. 核心线程满，排队队列满，临时线程满时，触发任务拒绝策略

线程池大小设置：

- cpu密集型运算——最大并行数+1
- I/O密集型运算——最大并行数*期望CPU利用率\*(CPU计算时间+等待时间)/CPU计算时间



