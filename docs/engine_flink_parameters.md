# engine-flink插件配置参数说明


| 参数名称 | 默认值 | 描述 |
|---|---|---|
| yarnAccepterTaskNumber|`1`| engine可接受的yarn上任务为`Accepted`状态的个数，如果超过指定的数量，engine会将未提交的任务加入到`lacking queue`中。等待数量降低至指定值后再提交到yarn上。|
| flinkJarPath ||`session check`健康检查所需要提交的jar的路径|
| checkSubmitJobGraphInterval |`0`|提交`session check`任务的时间间隔，时间单位为`s`。|
| flinkSessionName |`Flink session`|`flink session` 启动之后在yarn上显示的名称。|
| sessionRetryNum |`5`|当session在yarn上失败或者engine未检测到session时，engine尝试拉起session的次数。如果超过该次数则不会重试。|
| sessionStartAuto |`false`|是否由engine接管`flink session`,`false`代表由`EazyManager`接管，`true`代表由`Engine`接管。|
| flinkPluginRoot |`/data/insight_plugin/flinkplugin`|flink任务需要的`flinkx`以及`flinkStreamSql`插件路径。|
| monitorElectionWaitTime |`5000`|`flink client`加入到leader选举的等待时间，时间单位为`s`。|
| asyncCheckYarnClientThreadNum |`3`|`Engine`异步检查`YarnClient`是否健康的线程池数量。|



