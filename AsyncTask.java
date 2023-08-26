package thread.pool;

public abstract class AsyncTask<Result> extends Task{

    private Result result;

    public void setResult(Result result){
        this.result = result;
    }

    public Result getResult(){
        if(done) return result;
        else {
            System.out.println("任务未完成");
            return null;
        }
    }
}
