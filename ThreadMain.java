package thread.pool;


import thread.pool.Task;
import thread.pool.ThreadPool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadMain {
    public static void main(String[] args) throws InterruptedException {
        ThreadPool pool = new ThreadPool(10,3,0);
        pool.startService(1);
        Object lock = new Object();

        for (int i = 1; i <= 9; i++) {
            int finalI = i;
            pool.addTask(new Task() {
                @Override
                public void run() {
                    for (int i1 = 0; i1 < 100; i1++) {
                        System.out.println("thread "+ finalI+" count: "+i1);
                    }
                    System.out.println("thread "+ finalI +" start priority="+Thread.currentThread().getPriority());

                }

                public int priority() {
                    return finalI;
                }
            });
        }
/*        ReentrantLock rlock = new ReentrantLock();
        Condition condition = rlock.newCondition();
        final boolean[] c = {true};
        pool.addTask(new Task() {

            @Override
            public void run() {
                System.out.println("1");
                for (int i = 0; i < 10; i++) {
                    try {
                        rlock.lock();
                        while (!c[0]) condition.await();
                        System.out.println("A");
                        c[0] = false;
                        condition.signalAll();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }finally {
                        rlock.unlock();
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            public int priority(){
                return 5;
            }
        });
        pool.addTask(new Task() {
            @Override
            public void run() {
                System.out.println("2");
                for (int i = 0; i < 10; i++) {
                    try {
                        rlock.lock();
                        while (c[0]) condition.await();
                        System.out.println("B");
                        c[0] = true;
                        condition.signalAll();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }finally {
                        rlock.unlock();
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            public int priority(){
                return 6;
            }
        });*/
        //Thread.sleep(5000);
        pool.closeService();

    }


}
