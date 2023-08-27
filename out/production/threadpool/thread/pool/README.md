# 线程池
支持异步（或着：同步、异步混合），支持线程池动态扩展，支持缓冲队列及优先队列
## 启动
```java
//方法一
ThreadPool pool = new ThreadPool(10,3,1); // 10:任务队列最大长度(limit), 3:线程池容量(size)，1：可扩展临时线程数量(extend)
//方法二
ThreadPool pool = new ThreadPool(3,1);  //3:线程池容量，1：可扩展临时线程数量 (任务队列最大长度=100)
//方法三
ThreadPool pool = new ThreadPool(3); //3:线程池容量 (任务队列最大长度=100, 可扩展临时线程数量=0)
```

## 线程队列
> 实现线程队列接口：TaskQueueInterface
> 
> 已实现队列： 1. TaskQueue 默认队列  2.PriorityTaskQueue 优先队列（可设置优先级）
> 
> 已实现队列创建： ThreadFactory.createTaskQueue(int kind,int limit) kind=1(TaskQueue) or 其他int值（PriorityTaskQueue）, limit：队列最大容量
```java
pool.startService(1); //参数:队列类型，1(TaskQueue) or 其他int值（PriorityTaskQueue）
```
## 添加任务
```java
pool.addTask(new Task() {
    @Override
    public void run() {
            // do something
            //gettid() 获得执行线程的id,一般线程 'pool:1',扩展线程 'extend:1'
            }
    
    public int priority() {  //可以不写，默认5
            return 1; //优先值从大到小:10>1 ,max=10, mini=1
            }
});
```

## 线程池扩展
> 当队列中等待任务量到达上限，自动扩展线程，扩展数量=extend(见上面‘启动’)
> 当队列中等待任务小于ThreadPool.MINIMUM_CAPACITY, 扩展线程自动终止

## 运行时
```java
pool.currentOnTaskThreads();  // 执行任务中的线程数量
pool.currentOnWaitingThreads();  // 等待分配任务中的线程数量
pool.onActiveThreads();  //活跃的线程数量
pool.message() //获取当前线程池运行数据（活跃线程数量，执行中的线程数量，空闲线程数量，等待队列中的任务数量，已完成的任务数量）
        
task.cancel() //当任务处在等待队列中时，可以被取消
```
## 异步
> AsyncTask类 继承 Task类
```java
        AsyncTask<String> task = new AsyncTask<String>() { //这里设置返回类型为String
            @Override
            public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        setResult("i'm done 1 !!!");  //设置返回值
                    }catch (Exception e){
                        e.printStackTrace();
                    }
            }
        };
        
        pool.addTask(task)
        while (!task.isDone());  //判断任务是否执行完毕
        System.out.println(task.getResult());  //获取返回值
```
## 结束
```java
pool.closeService(); //关闭等待队列，等待剩余任务执行完毕
pool.shutdown();  //删除等待队列中的任务，等待所有线程执行完毕
```

### 大家觉得没问题麻烦给个星，如果有问题敬请提出，遇到了技术瓶颈不知道怎么提升，你的意见是对我最大的帮助，谢谢！！！

### qq交流群： 881241271

