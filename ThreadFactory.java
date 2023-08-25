package thread.pool;

public class ThreadFactory {

    /**
     * 任务线程
     * @param queue 任务队列
     * @param temporary
     * @return
     */
    public static TaskThread create(TaskQueueInterface queue,boolean temporary){
        return new TaskThread(queue,temporary);
    }

    /**
     * 任务队列
     * @param kind
     * @param limit
     * @return
     */
    public static TaskQueueInterface createTaskQueue(int kind,int limit){
        if (kind == 1) {
            return new PriorityTaskQueue(limit);
        }
        return new TaskQueue(limit);
    }


}
