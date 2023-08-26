package thread.pool;


import thread.pool.Task;
import thread.pool.ThreadPool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadMain {
    public static void main(String[] args) throws InterruptedException {
        ThreadPool pool = new ThreadPool(20,6,3);
        pool.startService(0);

        for (int i = 1; i <= 30; i++) {
            final int index = i;
            pool.addTask(new Task() {
                @Override
                public void run() {
                    for (int i1 = 0; i1 < 50; i1++) {
                        System.out.println("thread "+ gettId()+" count: "+i1);
                    }
                    System.out.println("done: "+index);
                }
            });
        }

        pool.closeService();

    }


}
