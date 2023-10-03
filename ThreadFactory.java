package thread.pool;

public class ThreadFactory {

    /**
     * 任务线程
     * @param queue 任务队列
     * @param temporary
     * @return
     */
    public static TaskThread create(TaskQueueInterface queue,boolean temporary, boolean recorde){
        return new TaskThread(queue,temporary, recorde);
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
        //TODO 此处可用if-else添加kind并返回自定义队列（必须继承TaskQueueInterface）
        return new TaskQueue(limit);
    }
}
