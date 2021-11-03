package thread.pool;

public class ThreadFactory {

    public static TaskThread create(TaskQueueInterface queue,boolean temporary){
        return new TaskThread(queue,temporary);
    }

    public static TaskQueueInterface createTaskQueue(int kind,int limit){
        if (kind == 1) {
            return new PriorityTaskQueue(limit);
        }
        return new TaskQueue(limit);
    }


}
