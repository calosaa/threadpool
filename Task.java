package thread.pool;

public abstract class Task{

    private String tId;  //thread id

    protected volatile boolean done = false;

    public void settId(String thId){
        this.tId = thId;
    }

    public String gettId(){
        return tId;
    }
    public abstract void run();

    public int priority(){
        return PriorityTaskQueue.NORMAL_PRIORITY;
    }

    public boolean isDone(){
        return this.done;
    }

    public void done(){
        this.done = true;
    }

}
