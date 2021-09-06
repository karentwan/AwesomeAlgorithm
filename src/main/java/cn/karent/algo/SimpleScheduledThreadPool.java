package cn.karent.algo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/***********************************************
 * description: 任务调度器简单实现
 * @author wan
 * @date 2021.09.05
 ***********************************************/
public class SimpleScheduledThreadPool {

    static Logger log = LoggerFactory.getLogger(SimpleScheduledThreadPool.class);

    // 时间单元
    private TimeUnit unit;
    // 每一个格子多长时间
    private long duration;
    // 有多少个时间隔(循环队列的长度)
    private int length;
    // 调度线程
    private Thread work;
    // 时间轮
    private PriorityQueue[] tasks;
    // 用来执行任务的线程池
    private ExecutorService pool;
    // 当前时间所在位置
    private int pos;

    static class Task {
        int round;
        Runnable task;

        public Task(int round, Runnable task) {
            this.round = round;
            this.task = task;
        }
    }

    public SimpleScheduledThreadPool() {
        unit = TimeUnit.SECONDS;
        duration = 1;
        length = 6;
        pool = Executors.newFixedThreadPool(4);
        tasks = new PriorityQueue[length];
        // 时间轮初始化
        for (int i = 0; i < length; i++) {
            tasks[i] = new PriorityQueue<Task>((k1, k2)->k1.round - k2.round);
        }
        work = new Thread(()->{
            // 开始进行调度
            long ms = unit.toMillis(duration);  // 线程调度运行间隔时间
            while (true) {
                log.info("执行任务....");
                PriorityQueue<Task> que = tasks[pos];
                synchronized (que) {
                    while (!que.isEmpty() && que.peek().round == 0) {
                        Task t = que.poll();
                        pool.execute(t.task);  // 执行任务
                    }
                    Iterator<Task> it = que.iterator();
                    while (it.hasNext()) {
                        Task t = it.next();
                        t.round--;
                    }
                }
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 时间轮计数
                pos = (pos + 1) % length;
            }
        }, "work");
        work.start();
    }

    public void execute(Runnable task, int interval) {
        int nextTime = pos + interval;
        int round = nextTime / length;
        int idx = nextTime % length;
        Task t = new Task(round, task);
        synchronized (tasks[idx]) {
            tasks[idx].offer(t);
        }
    }

    static int nextInt(int bound) {
        return ((int) (Math.random() * bound));
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleScheduledThreadPool pool = new SimpleScheduledThreadPool();
        Runnable task = ()->{
            log.info("=================>我是一个任务<=================");
        };
//        pool.execute(task, 1);
//        pool.execute(task, 3);
//        pool.execute(task, 5);
//        pool.execute(task, 6);
//        pool.execute(task, 7);
//        pool.execute(task, 11);
//        for (int i = 0; i < 100; i++) {
//            new Thread(()->{
//                try {
//                    Thread.sleep(nextInt(200));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                pool.execute(task, nextInt(120));
//            }).start();
//        }
        Thread.sleep(2000);
        for (int i = 0; i < 10000; i++) {
            try {
                Thread.sleep(nextInt(400));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pool.execute(task, nextInt(120));
        }
    }

}
