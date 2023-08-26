# 线程池
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
    
            }
    
    public int priority() {
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
```

## 结束
```java
pool.closeService(); //关闭等待队列，等待剩余任务执行完毕
pool.shutdown();  //删除等待队列中的任务，等待所有线程执行完毕
```




