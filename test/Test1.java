package thread.pool.test;

import thread.pool.Task;
import thread.pool.TaskThread;
import thread.pool.ThreadPool;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        ThreadPool tp = new ThreadPool(3);
        tp.setRecorde(true);
        tp.startService(0);

        tp.addTask(new Task() {
            @Override
            public void run() throws Exception{
                int a = 1/0;
            }
        });
        tp.addTask(new Task() {
            @Override
            public void run() throws Exception {
                System.out.println("do nothing");
            }
        });
        TimeUnit.SECONDS.sleep(3);
        Map<Integer, String> ids = tp.oncloseThreads();
        System.out.println(ids);
        System.out.println(tp.onActiveThreads());
        for (int i = 0; i < 3; i++) {
            TaskThread tt = tp.getThread(i);
            System.out.println("thread "+tt.gettId()+" info");
            List<String> infos = tt.getTaskInfos();
            System.out.println("info size : "+infos.size());
            if (!infos.isEmpty()){
                for (String info : infos) {
                    System.out.println(info);
                }
            }

        }

        System.out.println(tp.onActiveThreads());
        System.out.println(tp.oncloseThreads());
        tp.closeService();
    }
}
