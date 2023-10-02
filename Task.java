package thread.pool;

public abstract class Task{

    private String tId;  //thread id

    private static final String TASK_INFO = "Normal Task";
    private volatile boolean canceled = false;

    private volatile boolean start = false;
    protected volatile boolean done = false;

    public void settId(String thId){
        this.tId = thId;
    }

    public String gettId(){
        return tId;
    }
    public abstract void run() throws Exception;

    public int priority(){
        return PriorityTaskQueue.NORMAL_PRIORITY;
    }

    public String taskInfo() {
        return TASK_INFO;
    }

    public boolean isDone(){
        return this.done;
    }

    public void done(){
        this.done = true;
    }


    public void start(){
        this.start = true;
    }
    public void cancel(){
        if (!this.start) this.canceled = true;
        else System.out.println("任务已经开始执行，无法取消");
    }

    public boolean isCanceled(){
        return this.canceled;
    }

}
