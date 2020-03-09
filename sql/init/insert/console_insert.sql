insert into console_cluster
select * from console.console_cluster;


insert into console_engine
select * from console.console_engine;


insert into console_component
select * from console.console_component;

insert into console_dtuic_tenant
select * from console.console_dtuic_tenant;

insert into console_engine_tenant
select * from console.console_engine_tenant;

insert into console_queue
select * from console.console_queue;
