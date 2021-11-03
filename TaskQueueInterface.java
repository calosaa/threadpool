package thread.pool;

public interface TaskQueueInterface {
    int kind = 0;
    void push(Task task);
    Task pop();
    int getCount();

}
