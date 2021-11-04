package thread;


import thread.pool.Task;
import thread.pool.ThreadPool;

public class ThreadMain {
    public static void main(String[] args) throws InterruptedException {
        ThreadPool pool = new ThreadPool(10,5,2);
        pool.startService(1);
        Object lock = new Object();

        for (int i = 1; i <= 10; i++) {
            int finalI = i;
            pool.addTask(new Task() {
                @Override
                public void run() {
                    for (int i1 = 0; i1 < 100; i1++) {
                        System.out.println("thread "+ finalI+" count: "+i1);
                    }
                    System.out.println("thread "+ finalI +" start priority="+Thread.currentThread().getPriority());

                }

                /*public int priority() {
                    return finalI;
                }*/
            });
        }
        //Thread.sleep(5000);
        pool.closeService();

    }


}
