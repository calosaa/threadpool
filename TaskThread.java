package thread.pool;

public class TaskThread extends Thread{
    private volatile boolean running = true;
    private volatile boolean onTask = false;
    private final TaskQueueInterface taskQueue;
    private boolean temporary = false;
    private volatile boolean waiting;

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
        if(waiting)interrupt();
    }

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
