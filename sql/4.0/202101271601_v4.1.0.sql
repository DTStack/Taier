-- 数据源节点表
CREATE TABLE `node_cluster` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`ipOrDomain`  varchar(128) NOT NULL COMMENT 'ip或域名' ,
`port`  int(11) NOT NULL COMMENT '端口' ,
`realSourceId`  int(11) NOT NULL COMMENT '物理数据源id' ,
`is_deleted`  tinyint(2) NOT NULL DEFAULT 0 COMMENT '是否删除，0否，1是 ' ,
PRIMARY KEY (`id`)
);