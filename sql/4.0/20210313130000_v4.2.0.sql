update task_param_template set params = '## sql任务并发度设置
sql.env.parallelism=1

## 时间窗口类型（ProcessingTime或者EventTime）
time.characteristic=EventTime

## 窗口提前触发时间，单位为秒(填写正整数即可)
# early.trigger=1

## ttl状态控制
## 最小过期时间,大于0的整数,如1d、1h(dD:天,hH:小时,mM:分钟,ss:秒)
# sql.ttl.min=1h
## 最大过期时间,大于0的整数,如2d、2h(dD:天,hH:小时,mM:分钟,ss:秒),需同时设置最小时间,且比最小时间大5分钟
# sql.ttl.max=2h

## 生成checkpoint时间间隔（以毫秒为单位），默认:5分钟,注释掉该选项会关闭checkpoint生成
flink.checkpoint.interval=300000

## 设置checkpoint生成超时（以毫秒为单位），默认:10分钟
sql.checkpoint.timeout=600000

## 任务出现故障的时候一致性处理,可选参数EXACTLY_ONCE,AT_LEAST_ONCE；默认为EXACTLY_ONCE
# sql.checkpoint.mode=EXACTLY_ONCE

## 最大并发生成 checkpoint 数量，默认：1 次
# sql.max.concurrent.checkpoints=1

## checkpoint 外存的清理动作
## true（任务结束之后删除checkpoint外部存储信息）
## false（任务结束之后保留checkpoint外部存储信息）
sql.checkpoint.cleanup.mode=false

## jobManager配置的内存大小，默认1024（单位M）
# jobmanager.memory.mb=1024

## taskManager配置的内存大小，默认1024（单位M）
# taskmanager.memory.mb=1024

## taskManager 对应 slot的数量
slots=1

## logLevel: error,debug,info(默认),warn
logLevel=info

## Watermark发送周期，单位毫秒
# autoWatermarkInterval=200

## 设置输出缓冲区的最大刷新时间频率（毫秒）
# sql.buffer.timeout.millis=100

## 任务优先级, 值越小，优先级越高，范围:1-1000
job.priority=10

## kafka kerberos 数据源开启Kerberos
## security.kerberos.login.contexts=Client,KafkaClient


## 异步访问维表是否开启连接池共享,开启则 1.一个tm上多个task共享该池, 2.一个tm上多个url相同的维表单/多个task共享该池 (默认false)
# async.side.clientShare=false
## 连接池中连接的个数,上面参数为true才生效(默认5)
# async.side.poolSize=5'
where engine_type = 0 and compute_type = 0 and task_type = 0;