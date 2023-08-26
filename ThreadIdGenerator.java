package thread.pool;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadIdGenerator{
    private static final AtomicInteger count = new AtomicInteger();
    private static final AtomicInteger tCount = new AtomicInteger();
    public static String generate(boolean temp){
        if(!temp) return "pool:"+count.incrementAndGet();
        else return "extend:"+tCount.incrementAndGet();
    }
}
