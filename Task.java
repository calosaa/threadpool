package thread.pool;

public abstract class Task {
    public abstract void run();

    public int priority(){
        return PriorityTaskQueue.NORMAL_PRIORITY;
    }


}
