-- 如果插入的数据会导致UNIQUE索引或PRIMARY KEY发生冲突/重复，则忽略此次操作/不插入数据

insert IGNORE into console_cluster
select * from console.console_cluster;

insert IGNORE into console_engine
select * from console.console_engine;

insert IGNORE into console_component
select * from console.console_component;

insert IGNORE into console_dtuic_tenant
select * from console.console_dtuic_tenant;

insert IGNORE into console_engine_tenant
select * from console.console_engine_tenant;

insert IGNORE into console_queue
select * from console.console_queue;
