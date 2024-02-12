package thread.pool;

import java.util.Objects;
import java.util.function.Function;

public interface AddHandler {
    boolean handle(TaskQueueInterface queue, Task task, ExtendPoolInterface extend);
}
