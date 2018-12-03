// 帮助文档
import React from 'react'
/* eslint-disable */
export const dataFilterDoc = (
    <div>
        where 条件即针对源头数据筛选条件，根据指定的 column、table、where 条件拼接 SQL 进行数据抽取，暂时不支持limit关键字过滤。利用 where 条件可进行全量同步和增量同步，具体说明如下：<br/>
        <ul>
            <li>1）全量同步：第一次做数据导入时通常为全量导入，可不用设置 where 条件。</li>
            <li>2）增量同步：增量导入在实际业务场景中，往往会选择当天的数据进行同步，通常需要编写 where 条件语句，请先确认表中描述增量字段（时间戳）为哪一个。如tableA增量的字段为create_time，则填写create_time > 您需要的日期，如果需要日期动态变化，请参考帮助文档。</li>
        </ul>
    </div>
)

export const switchKey = (
    <ul>
        <li>MySQL：支持数值型切分键</li>
        <li>Oracle：支持数值型、字符串类型切分键</li>
    </ul>
)

export const hdfsPath = (
    <div>
        user/hive/warehouse/projectName.db/<br/>tableName
        是RD-OS默认的HDFS文件组织方式，projectName为项目名，
        其中每个tableName是HDFS内的一个目录，储存着一张表的数据。
        如果此表的数据存储在当前项目空间内，只需修改tableName即可，
        否则需要根据HDFS的存储位置填写。
    </div>
)

export const jobSpeedLimit = ( // 作业上限速度
    <div>
        设置作业速率上限，则数据同步作业的总速率将尽可能按照这个上限进行同步，
        需根据实际硬件配置调整，默认为1
    </div>
)

export const jobConcurrence = ( // 作业并发数
    <ul>
        <li> 1.作业速率上限=作业并发数单作业的传输速率，当作业速率上限已定，选择的并发数越高则单并发的速率越低，同时所占用的内存会越高，这可以根据你的业务需求选择设定的值</li>
        <li>2.作业并发数下拉框选择对象，取决作业速率上限。作业速率上限选择值nMB/s，作业并发数最大能选择n</li>
    </ul>
)

export const errorCount = ( // 作业并发数
    <div>
        表示脏数据的最大容忍条数，如果您配置0，
        则表示严格不允许脏数据存在；如果不填则代表容忍脏数据
    </div>
)

export const hdfsConfig = ( // hdfs config
    <div>
        高可用模式下的填写规则：
        <ul>
            <li>
                1、分别要填写：nameservice名称、
                namenode名称（多个以逗号分隔）、proxy.provider参数；</li>
            <li>2、所有参数以JSON格式填写；</li>
            <li>3、格式为：<br/>
                "dfs.nameservices": "nameservice名称",
                "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔",
                "dfs.namenode.rpc-address.nameservice名称.namenode名称": "",
                "dfs.namenode.rpc-address.nameservice名称.namenode名称": "",
                "dfs.client.failover.proxy.provider.<br/>
                nameservice名称": "org.apache.hadoop.<br/>
                hdfs.server.namenode.ha.<br/>
                ConfiguredFailoverProxyProvider"
            </li>
            <li>
                4、详细参数含义请参考《帮助文档》或
                <a href="http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html"
                    target="blank">Hadoop官方文档</a>
            </li>
        </ul>
    </div>
)

export const rdbConfig = ( // relational db config
    <div>
        JDBC URL配置参考：
        <ul>
            <li>MySQL: jdbc:mysql://host:port/batch?useCursorFetch=true</li>
            <li>Oracle: jdbc:oracle:thin:@host:port:ORCL</li>
            <li>Hive: jdbc:hive2://host:port/projectName</li>
        </ul>
    </div>
)

export const hBaseConfig = ( // HBase configuration
    <div>
        HBase配置说明：
        <ul>
            <li>1、Zookeeper 集群的地址，多个地址间用逗号分割。例 如：" IP1:Port, IP2:Port, IP3:Port/子目录".默认是 localhost,是给伪分布式用的。要修改才能在完全分布式的情况下使用。如果在hbase-env.sh设置了HBASEMANAGESZK， 这些ZooKeeper节点就会和HBase一起启动</li>
            <li>2、Port: ZooKeeper的zoo.conf中的配置。客户端连接的端口， 默认2181</li>
            <li>3、子目录：HBase在ZooKeeper中配置的子目录</li>
        </ul>
    </div>
)

export const recordDirtyData = (
    <div>
        <p>保存到默认位置，您可以在“数据管理-脏数据管理”中查看，表名：defaultName（生命周期：30天）</p>
    </div>
)

export const errorPercentConfig = (
    <div>
        <p>任务执行结束后统计错误记录占比，当比例过高时，将此任务实例置为失败</p>
    </div>
)
