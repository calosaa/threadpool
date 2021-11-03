package thread.pool;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaskQueue implements TaskQueueInterface{
    private final Task [] list;
    private final int size;
    private volatile int start = 0;
    private volatile int end = 0;
    private volatile int count = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Condition writeCondition = lock.writeLock().newCondition();

    public TaskQueue(int limit){
        this.size = limit + 1;
        this.list = new Task[limit+1];
    }

    @Override
    public void push(Task t) {
        try {
            lock.writeLock().lock();
            while((end+1)%size==start) writeCondition.await();
            list[end] = t;
            end = (end+1)%size;
            count++;
            System.out.println("输入");
            writeCondition.signalAll();
        }catch (InterruptedException ignored){
        }finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Task pop(){
        try {
            lock.writeLock().lock();
            while(end==start) writeCondition.await();
            Task t = list[start];
            start = (start+1)%size;
            count--;
            System.out.println("输出");
            writeCondition.signalAll();
            return t;
        }catch (InterruptedException e){
            return null;
        }finally {
            lock.writeLock().unlock();
        }

    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        try {
            lock.readLock().lock();
            System.out.println(count);
            return count;
        }finally {
            lock.readLock().unlock();
        }
    }

}
