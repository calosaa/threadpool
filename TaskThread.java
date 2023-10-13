package thread.pool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskThread extends Thread{

    private static final SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String id;
    private volatile boolean running = true;
    private volatile boolean onTask = false;
    private final TaskQueueInterface taskQueue;
    private boolean temporary = false;
    private volatile boolean waiting = true;

    private List<String> taskInfos;
    private boolean recorde = false;

    @Deprecated
    public TaskThread(TaskQueueInterface queue){
        this.taskQueue = queue;
    }

    public TaskThread(TaskQueueInterface queue,boolean temporary){
        this.taskQueue = queue;
        this.temporary = temporary;
        this.id = ThreadIdGenerator.generate(temporary);
    }
    public TaskThread(TaskQueueInterface queue,boolean temporary, boolean tRecorde){
        this.taskQueue = queue;
        this.temporary = temporary;
        this.id = ThreadIdGenerator.generate(temporary);
        if(tRecorde){
            this.taskInfos = new ArrayList<>();
            this.recorde = true;
        }
    }
    @Override
    public void run() {

        while (running && !Thread.currentThread().isInterrupted()){
            if (checkTaskContains()) break;
            waiting = true;
            Task task = taskQueue.pop();
            waiting = false;
            if(task!=null) {
                String exp = ""; // exception
                onTask = true;
                setPriority(task.priority());
                task.settId(this.id);
                if(!task.isCanceled()) {
                    task.start();
                    try {
                        task.run();
                    } catch (Exception e) {
                        exp = e.getMessage(); //TODO 此处可修改返回异常Message或StackTrace
                        //exp = getStackTraceAsString(e);
                    }
                    task.done();
                    ThreadPool.finish.incrementAndGet();
                }
                if (this.recorde) {
                    String log = "{thread Id : "+gettId()+", task info : "+task.taskInfo()+", isDone : "+(!task.isCanceled())+", time : "+sdf.format(System.currentTimeMillis())+", exception : "+exp+"}";
                    this.taskInfos.add(log);//TODO 此行可替换为日志，则taskInfos不可用
                    task.setLog(log);
                }
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

    public boolean getRunning(){
        return running;
    }

    public String gettId(){
        return id;
    }

    public List<String> getTaskInfos(){
        return this.taskInfos;
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
