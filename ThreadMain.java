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

        /**for (int i = 1; i <= 30; i++) {
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
        System.out.println(pool.message());
        pool.closeService();**/

        //异步获取
        AsyncTask<String> task = new AsyncTask<String>() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    setResult("i'm done 1 !!!");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        AsyncTask<String> task2 = new AsyncTask<String>() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    setResult("i'm done 2 !!!");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        pool.addTask(task);
        pool.addTask(task2);
        task.cancel();
        while (!task.isDone() && !task2.isDone());
        System.out.println(task.getResult());
        System.out.println(task2.getResult());
        pool.closeService();

    }


}
