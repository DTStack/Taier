ALTER TABLE `console_queue`
 MODIFY COLUMN `queue_name` varchar(128)  NOT NULL COMMENT '队列名称';
ALTER TABLE `console_cluster`
    MODIFY COLUMN `cluster_name` varchar(128) NOT NULL COMMENT '集群名称';