package thread.pool;

public class TaskThread extends Thread{
    private volatile boolean running = true;
    private volatile boolean onTask = false;
    private final TaskQueueInterface taskQueue;
    private boolean temporary = false;
    private volatile boolean waiting = true;

    public TaskThread(TaskQueueInterface queue){
        this.taskQueue = queue;
    }
    public TaskThread(TaskQueueInterface queue,boolean temporary){
        this.taskQueue = queue;
        this.temporary = temporary;
    }
    @Override
    public void run() {

        while (running && !Thread.currentThread().isInterrupted()){
            if (checkTaskContains()) break;
            waiting = true;
            Task task = taskQueue.pop();
            waiting = false;
            if(task!=null) {
                onTask = true;
                setPriority(task.priority());
                task.run();
                onTask = false;
            }
        }
    }

    public boolean getOnTask(){
        return onTask;
    }
    public void close(){
        this.running = false;
        interrupt();
    }

    /**
     * 当此线程为 临时或扩展（temporary变量标记）线程 且 任务队列计数<ThreadPool.MINIMUM_CAPACITY时，返回true, 自动关闭此线程
     * @return
     */
    public boolean checkTaskContains(){
        if(temporary && taskQueue.getCount()<ThreadPool.MINIMUM_CAPACITY){
            return true;
        }
        return false;

    }

    public boolean getWaiting(){
        return waiting;
    }
}
