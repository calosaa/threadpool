package thread.pool;

public interface TaskQueueInterface {
    void push(Task task);
    Task pop();
    int getCount();

    boolean isEmpty();


}
