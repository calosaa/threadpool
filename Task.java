package thread.pool;

public abstract class Task {

    private String tId;  //thread id

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


}
