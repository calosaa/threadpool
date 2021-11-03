package thread.pool;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PriorityTaskQueue implements TaskQueueInterface{
    public static final int MAX_PRIORITY = 10;
    public static final int NORMAL_PRIORITY = 5;
    public static final int MIN_PRIORITY = 1;
    private final TaskNode taskLink;
    private volatile int count;
    private final int limit;
    int kind = 1;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Condition writeCondition = lock.writeLock().newCondition();

    public PriorityTaskQueue(int limit){
        this.taskLink = new TaskNode();
        this.taskLink.task = new Task() {

            @Override
            public void run() {
                //head
            }

            @Override
            public int priority() {
                return MAX_PRIORITY;
            }
        };
        this.count = 0;
        this.limit = limit;
    }

    @Override
    public void push(Task task) {
        try{
            lock.writeLock().lock();
            while (count>=limit) writeCondition.await();
            TaskNode p = this.taskLink;
            while (p.next!=null){
                if(p.next.compareTo(task)) p = p.next;
                else break;
            }
            TaskNode node = new TaskNode();
            node.task = task;
            if(p.next!=null)node.next = p.next;
            p.next = node;
            count++;
            writeCondition.signalAll();
        }catch (InterruptedException ignored){}
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Task pop() {
        try {
            lock.writeLock().lock();
            while (count==0) writeCondition.await();
            TaskNode p = this.taskLink.next;
            this.taskLink.next = this.taskLink.next.next;
            count--;
            writeCondition.signalAll();
            return p.task;
        }catch (InterruptedException e){
            return null;
        }finally {
            lock.writeLock().unlock();
        }
    }

    static class TaskNode{
        Task task;
        TaskNode next;

        public boolean compareTo(Task o) {
            return task.priority()>o.priority();
        }
    }

    @Override
    public int getCount() {
        try {
            lock.readLock().lock();
            return count;
        }finally {
            lock.readLock().unlock();
        }

    }
}
