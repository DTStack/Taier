-- 将lineage_table_table表的input_table_key和result_table_key长度设为256
ALTER TABLE `lineage_table_table` CHANGE COLUMN `input_table_key` `input_table_key` varchar(256) NOT NULL COMMENT '输入表表物理定位码',
 CHANGE COLUMN `result_table_key` `result_table_key` varchar(256) NOT NULL COMMENT '输出表表物理定位码';


 -- 将lineage_column_column表的input_table_key和result_table_key长度设为256
 ALTER TABLE `lineage_column_column` CHANGE COLUMN `input_table_key` `input_table_key` varchar(256) NOT NULL COMMENT '输入表表物理定位码',
 CHANGE COLUMN `result_table_key` `result_table_key` varchar(256) NOT NULL COMMENT '输出表表物理定位码';