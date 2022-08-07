package thread.pool;

public class ThreadPool {

    public static final int MINIMUM_CAPACITY = 3;// close extend pool when smaller than this

    private TaskQueueInterface queue;
    //任务队列长度
    private int limit = 100;
    private TaskThread[] threadPool;
    //线程池容量
    private final int poolSize;
    //扩展容量
    private int extend = 0;

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
            this.threadPool[i] = ThreadFactory.create(this.queue,false);
            this.threadPool[i].start();
        }
    }

    public boolean addTask(Task task){
        if(queue.getCount()==limit){
            extendPool();
        }
        if(queue.getCount()<limit) {
            queue.push(task);
            return true;
        }else return false;
    }

    public void extendPool(){
        if(extend>0){
            for (int i = 0; i < extend; i++) {
                if(this.threadPool[i+poolSize]==null || !this.threadPool[i+poolSize].isAlive()) {
                    this.threadPool[i + poolSize] = ThreadFactory.create(queue, true);
                    this.threadPool[i + poolSize].start();
                }
            }
            System.out.println("扩展");
        }
    }


    public int currentOnTaskThread(){
        int count = 0;
        for (int i = 0; i < (poolSize + extend); i++) {
            if(this.threadPool[i]!=null && this.threadPool[i].getOnTask()) count++;
        }
        return count;
    }

    public int currentOnWaitingThread(){
        int count = 0;
        for (int i = 0; i < (poolSize + extend); i++) {
            if(this.threadPool[i]!=null && this.threadPool[i].getWaiting()) count++;
        }
        return count;
    }
    public int onActiveThreads(){
        int count = 0;
        for (int i = 0; i < (poolSize + extend); i++) {
            if(this.threadPool[i].isAlive()) count++;
        }
        System.out.println("alive = "+count);
        return count;
    }

    public void closeService(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (queue.getCount()!=0);
                System.out.println("cache list size: "+queue.getCount());
                for (int i = 0; i < (poolSize + extend); i++) {
                    if(threadPool[i]!=null)threadPool[i].close();
                }
                queue = null;
                threadPool = null;
            }
        }).start();
    }

}
