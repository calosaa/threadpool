package thread.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {

    public static final int MINIMUM_CAPACITY = 3;// 任务队列的最小容量，当当前任务计数小于此值时，自动关闭扩展池，详见 #TaskThread.checkTaskContains()

    private TaskQueueInterface queue;
    //任务队列长度
    private int limit = 100;
    private TaskThread[] threadPool;
    //线程池容量
    private final int poolSize;
    //扩展容量
    private int extend = 0;

    private boolean stop = false;

    public static final AtomicInteger finish = new AtomicInteger();

    private boolean recorde = false;



    public ThreadPool(int size){
        this.poolSize = size;
    }


    public ThreadPool(int size,int extend){
        this.poolSize = size;
        this.extend = extend;
    }

    public ThreadPool(int limit,int size,int extend){
        this.limit = limit;
        this.poolSize = size;
        this.extend = extend;
    }

    public void startService(int queueKind){
        this.threadPool = new TaskThread[poolSize+extend];
        this.queue = ThreadFactory.createTaskQueue(queueKind,limit);
        for (int i = 0; i < poolSize; i++) {
            this.threadPool[i] = ThreadFactory.create(this.queue,false, recorde);
            this.threadPool[i].start();
        }
    }

    public void setRecorde(boolean r){
        this.recorde = r;
    }
    /**
     * 添加任务
     * @param task
     * @return
     */
    public boolean addTask(Task task){
        if (stop){
            System.out.println("线程池已关闭");
            return false;
        }
        if(queue.getCount()==limit){
            extendPool();
        }
        if(queue.getCount()<limit) {
            queue.push(task);
            return true;
        }else {
            System.out.println("添加失败");
            return false;
        }
    }

    /**
     * 扩展线程池数量（增加临时线程），扩展数量<extend，默认为0
     */
    public void extendPool(){
        if(extend>0){
            System.out.println("尝试扩展线程");
            for (int i = 0; i < extend; i++) {
                if(this.threadPool[i+poolSize]==null || !this.threadPool[i+poolSize].isAlive()) {
                    this.threadPool[i + poolSize] = ThreadFactory.create(queue, true, recorde);
                    this.threadPool[i + poolSize].start();
                    System.out.println("扩展线程+1");
                }
            }

        }
    }

    /**
     * 正在执行任务中的线程计数
     * @return
     */
    public int currentOnTaskThreads(){
        int count = 0;
        for (int i = 0; i < (poolSize + extend); i++) {
            if(this.threadPool[i]!=null && this.threadPool[i].getOnTask()) count++;
        }
        return count;
    }

    /**
     * 等待接收任务中的线程计数
     * @return
     */
    public int currentOnWaitingThreads(){
        int count = 0;
        for (int i = 0; i < (poolSize + extend); i++) {
            if(this.threadPool[i]!=null && this.threadPool[i].getWaiting()) count++;
        }
        return count;
    }

    /**
     * 活跃线程计数
     * @return
     */
    public int onActiveThreads(){
        int count = 0;
        for (int i = 0; i < (poolSize + extend); i++) {
            if(this.threadPool[i].isAlive()) count++;
        }
        System.out.println("alive = "+count);
        return count;
    }

    public Map<Integer, String> oncloseThreads(){
        Map<Integer, String> closeIds = new HashMap<>();
        for (int i = 0; i < (poolSize + extend); i++) {
            TaskThread t = this.threadPool[i];
            if(!t.getRunning() || !this.threadPool[i].isAlive()) closeIds.put(i, t.gettId());
        }
        return closeIds;
    }

    public void reCreate(int idx){  // idx : the index of thread in threadpool
        TaskThread itt = this.threadPool[idx];
        if(itt != null && !itt.getRunning()){
            boolean temp = idx > this.poolSize;
            this.threadPool[idx] = ThreadFactory.create(this.queue, temp, recorde);
            this.threadPool[idx].start();
        }else{
            if(itt != null){
                System.out.printf("idx="+idx+" not exists");
            }else {
                System.out.printf("idx=" + idx + " is running");
            }
        }
    }
    /**
     * 关闭线程池，等待任务队列计数为空，将运行线程参数running设置为false, 任务完成自动结束
     */
    public void closeService(){
        stop = true;
        new Thread(() -> {
            while (queue.getCount()!=0);
            for (int i = 0; i < (poolSize + extend); i++) {
                if(threadPool[i]!=null)threadPool[i].close();
            }
            queue = null;
            threadPool = null;
        }).start();
    }

    public void shutdown(){
        stop = true;
        new Thread(() -> {
            for (int i = 0; i < (poolSize + extend); i++) {
                if (threadPool[i] != null) threadPool[i].close();
            }
            queue = null;
            threadPool = null;
        }).start();
    }

    public int getWaitingTasks(){
        return queue.getCount();
    }

    public String message(){
        StringBuilder sb = new StringBuilder();
        sb.append("active threads: ")
                .append(onActiveThreads())
                .append(", running tasks: ")
                .append(currentOnTaskThreads())
                .append(", free threads: ")
                .append(currentOnWaitingThreads())
                .append(", waiting tasks: ")
                .append(getWaitingTasks())
                .append(", finished: ")
                .append(finish.get());
        return sb.toString();
    }

    public TaskThread getThread(int id){
        if(id>poolSize+extend){
            System.out.println("index > size");
            return null;
        }
        return this.threadPool[id];
    }
}
