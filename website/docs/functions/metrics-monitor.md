---
title: 指标监控
sidebar_label: 指标监控
---

# 指标监控


可观测的性能指标监控是数栈平台指标监控的指标维度数据之一，本文主要从实现框架介绍接入逻辑。

## 一 构建可观测的线程池框架

### 1 线程池监控

核心原理主要依赖动态 Proxy 和切面处理，从而监控对应线程池的核心数据，包括线程池的核心参数信息，以及计算线程池对应 Runnable 执行耗时的 RT 计算性能指标，并通过配置指标输出策略进行指标推送和指标监控。

框架封装：`taier-metrics`

#### 1.1 指标输出

目前主要支持三种模式的指标推送：

- `MICROMETER`：该模式下会配合 micrometer 框架和 prometheus，将对应的监控指标推送到 prometheus。
- `LOGGING`：该模式下将对应的监控指标以 log 日志的形式进行打印。
- `OUTPUT`：该模式下将对应的监控指标以 `System.out` 的形式打印到当前 PID 进程下。

#### 1.2 线程池模式

可观测线程监控主要通过代理原生的 `ThreadPoolExecutor` 进行指标监控，目前主要内置了两种线程池模式：

- `common` 模式：对应线程池 `EngineExecutor`，`EngineExecutor` 是该框架的核心顶层设计类，其他类都继承自该类。`common` 模式是默认的线程池模式，适用于 CPU 密集型场景，当核心线程数满了优先放入队列等待。
- `eager` 模式：对应线程池 `EagerEngineExecutor`，`eager` 模式适用于 IO 密集型场景，在线程池没达到设置的最大值之前优先创建新线程执行任务，而不是放入队列等待，比如 tomcat 线程池、dubbo 线程池都是采用这种模式。

#### 1.3 线程池指标数据

- `poolName`：当前线程池的名称，唯一标识。
- `poolAliasName`：当前线程池的别名。
- `corePoolSize`：核心线程数。
- `maximumPoolSize`：最大线程数。
- `keepAliveTime`：空闲时间。
- `queueType`：队列类型。
- `queueCapacity`：队列容量。
- `queueSize`：队列任务数。
- `fair`：`SynchronousQueue` 队列模式。
- `queueRemainingCapacity`：队列剩余容量。
- `activeCount`：正在执行任务的活跃线程大致总数。
- `taskCount`：大致任务总数。
- `completedTaskCount`：已执行完成的大致任务总数。
- `largestPoolSize`：池中曾经同时存在的最大线程数量。
- `poolSize`：当前池中存在的线程总数。
- `waitTaskCount`：等待执行的任务数量。
- `rejectCount`：拒绝的任务数量。
- `rejectHandlerName`：拒绝策略名称。
- `runTimeoutCount`：执行超时任务数量。
- `queueTimeoutCount`：在队列中等待超时的数量。
- `tps`：TPS。
- `maxRt`：最大执行耗时。
- `minRt`：最小执行耗时。
- `avg`：平均执行耗时。

#### 1.4 线程池指标示例

```java
// 1. start a monitor
TpMonitor sm = new TpMonitor();
sm.interval(20, 0, Collections.singletonList("output"));

// set timeout
int timeout = 1000;

// 2. build a metricsThreadPoolExecutor
EngineExecutor eager = ThreadPoolBuilder.newBuilder()
    .threadPoolName("ClientProxy") // 当前线程池名称，唯一标识
    .corePoolSize(1) // 核心线程数
    .maximumPoolSize(1) // 最大线程数
    .workQueue(QueueTypeEnum.SYNCHRONOUS_QUEUE.getName(), null, false) // 线程队列数据
    .queueTimeout(timeout) // 队列超时时间
    .keepAliveTime(7200000)
    .rejectEnhanced(true) // 拒绝策略增强，内部会通过 Proxy 动态代理构建并进行指标统计
    .rejectedExecutionHandler(new BlockCallerTimeoutPolicy(timeout))
    .runTimeout(timeout) // 查询超时
    // .eager(false) // eager 模式，IO 密集型，默认 false
    .buildDynamic()
    .registry(); // register by build

ThreadPoolExecutor executorService = new ThreadPoolExecutor(
    1,
    1,
    7200000,
    TimeUnit.MILLISECONDS,
    new SynchronousQueue<>(),
    new CustomThreadFactory("xingyi"),
    new BlockCallerTimeoutPolicy(timeout)
);

// run 10s, put 5s Future timeout 5s
for (int i = 0; i < 20; i++) {
    try {
        JobResult jobResult = CompletableFuture.supplyAsync(() -> {
            try {
                return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>() {
                    @Override
                    public JobResult execute() throws Exception {
                        System.out.println(Thread.currentThread().getName() + " : execute");
                        Thread.sleep(30000);
                        System.out.println(Thread.currentThread().getName() + " : FINISH");
                        return new JobResult();
                    }
                }, Thread.currentThread().getContextClassLoader(), true);
            } catch (Exception e) {
                throw new RdosDefineException(e);
            }
        }, eager).get(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | RejectedExecutionException e) {
        System.out.println(e.getMessage());
    } catch (TimeoutException e) {
        System.out.println("timeout");
    }
}

for (int i = 0; i < 20; i++) {
    try {
        JobResult jobResult = CompletableFuture.supplyAsync(() -> {
            try {
                return ClassLoaderCallBackMethod.callbackAndReset(new CallBack<JobResult>() {
                    @Override
                    public JobResult execute() throws Exception {
                        System.out.println(Thread.currentThread().getName() + " : execute");
                        Thread.sleep(200);
                        System.out.println(Thread.currentThread().getName() + " : FINISH");
                        return new JobResult();
                    }
                }, Thread.currentThread().getContextClassLoader(), true);
            } catch (Exception e) {
                throw new RdosDefineException(e);
            }
        }, eager).get(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | RejectedExecutionException e) {
        System.out.println(e.getMessage());
    } catch (TimeoutException e) {
        System.out.println("timeout");
    }
}

Thread.sleep(2000000L);
```

指标打印：

```java
system meter collect: ThreadPoolStats{poolName='ThreadPool', poolAliasName='null', corePoolSize=1, maximumPoolSize=1, keepAliveTime=7200000000, queueType='SynchronousQueue', queueCapacity=0, queueSize=0, fair=false, queueRemainingCapacity=0, activeCount=0, taskCount=12, completedTaskCount=12, largestPoolSize=1, poolSize=1, waitTaskCount=0, rejectCount=29, rejectHandlerName='BlockCallerTimeoutPolicy', dynamic=false, runTimeoutCount=1, queueTimeoutCount=0, tps=1.1, maxRt=30024, minRt=203, avg=1953.5455}
system meter collect: ThreadPoolStats{poolName='ThreadPool', poolAliasName='null', corePoolSize=1, maximumPoolSize=1, keepAliveTime=7200000000, queueType='SynchronousQueue', queueCapacity=0, queueSize=0, fair=false, queueRemainingCapacity=0, activeCount=0, taskCount=12, completedTaskCount=12, largestPoolSize=1, poolSize=1, waitTaskCount=0, rejectCount=29, rejectHandlerName='BlockCallerTimeoutPolicy', dynamic=false, runTimeoutCount=1, queueTimeoutCount=0, tps=0.0, maxRt=0, minRt=0, avg=0.0}
```

#### 1.5 MICROMETER 指标示例

当配置MICROMETER指标时，并实现扩展MicroMeterHandler，会自动收集线程池指标。
配置参数:
- taier.monitor.metrics.support.type=micrometer
- taier.monitor.metrics.enabled=true
- taier.monitor.metrics.prometheus.pushgateway.url=http://localhost:9091/metrics # pushgateway地址
- taier.monitor.metrics.prometheus.pushgateway.timeout=5 # pushgateway超时时间, 单位s 默认5s
- taier.monitor.metrics.prometheus.pushgateway.interval=60 # pushgateway 默认间隔时间,单位s, 默认60s
