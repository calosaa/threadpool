package thread.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PriorityTaskQueue implements TaskQueueInterface{
    public static final int MAX_PRIORITY = 10;
    public static final int NORMAL_PRIORITY = 5;
    public static final int MIN_PRIORITY = 1;
    private final TaskNode taskLink;
    private volatile AtomicInteger count = new AtomicInteger();

    private final int limit;

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
                return 11;
            }
        };
        //this.count = 0;
        count.set(0);
        this.limit = limit;
    }

    @Override
    public void push(Task task) {
        try{
            lock.writeLock().lock();
            while (count.get()>=limit) writeCondition.await();
            TaskNode p = this.taskLink;
            while (p.next!=null){
                if(p.next.compareTo(task)) p = p.next;
                else break;
            }
            TaskNode node = new TaskNode();
            node.task = task;
            if(p.next!=null)node.next = p.next;
            p.next = node;
            count.incrementAndGet();
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
            while (count.get()==0) writeCondition.await();
            TaskNode p = this.taskLink.next;
            this.taskLink.next = this.taskLink.next.next;
            count.decrementAndGet();
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
            return count.get();
        }finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public boolean isEmpty() {
        return getCount()==0;
    }
}
