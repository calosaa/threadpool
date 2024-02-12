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
### 2023/10/02 更新
1. 新增线程重启功能
```java
threadpool.reCreate(idx); //idx : 线程池中线程的索引
```
2. 新增线程任务运行记录功能,捕获Task中的异常
```java
 ThreadPool tp = new ThreadPool(3);
tp.setRecorde(true); // 在这里设置记录功能
tp.startService(0);

List<String> infos = taskThread.getTaskInfos(); //获取线程运行记录
//{info : Normal Task, done : true, time : 2023-10-02 19:08:39, exception : / by zero}

//重写Task类中的 #public String taskInfo()； 方法来设置对task的描述，该描述将会记录在线程运行记录中的info值，默认值为 ： Normal Task
#Task.class
public String taskInfo() {
        return TASK_INFO;
        }
#
```
3.新增获取停止运行的线程的索引及 ThreadId
```java
Map<Integer, String> closeIds = threadPool.oncloseThreads();
```
4.新增获取线程
```java
threadPool.getThread(idx); //idx : 线程索引
```
5.新增TODO建议
项目中一些字符串打印备注（标记为TODO）了可替换为Log，可以在ThreadFactory中#createTaskQueue方法添加自定义任务队列
### 2023/10/13 更新
task类可直接获取log > task.getLog()

### 204/2/12 更新
1. extendPool()由无返回值修改为返回int值，该值表示实际扩展线程数量
2. 可自定义阻塞队列添加任务的方式
使用方法：调用ThreadPool.addTask(Task task,AddHandler handler)方法，传入自定义handler（实现AddHandler接口),该接口传入阻塞队列，当前添加任务，以及ThreadPool.extendPool()方法。
在handler中使用ThreadPool.extendPool()方法：
```java
boolean handle(TaskQueueInterface queue, Task task, ExtendPoolInterface extend) {
    //do something
    //使用传入参数extend
    int extendCount = extend.extendPool();
    //do something and return
    
}

```
关于有些任务占用过长时间，建议另启动新线程池，不要混用