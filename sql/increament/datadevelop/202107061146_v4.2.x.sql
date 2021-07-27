--  迁移离线默认数据源的schema名称到数据源中心
--  hadoop引擎
update dt_pub_service.dsc_info a 
INNER JOIN ide.rdos_batch_data_source_center b on b.is_deleted=0 and b.is_default=1 and b.dt_center_source_id=a.id
inner join ide.rdos_project_engine c on c.project_id=b.project_id  and c.engine_type = 1
set a.schema_name = c.engine_identity
where a.is_deleted=0 and a.data_type in ('Hive','SparkThrift','Impala');

--  LibrA
update dt_pub_service.dsc_info a 
INNER JOIN ide.rdos_batch_data_source_center b on b.is_deleted=0 and b.is_default=1 and b.dt_center_source_id=a.id
inner join ide.rdos_project_engine c on c.project_id=b.project_id  and c.engine_type = 2
set a.schema_name = c.engine_identity
where a.is_deleted=0 and a.data_type_code =21;

--  TiDB
update dt_pub_service.dsc_info a 
INNER JOIN ide.rdos_batch_data_source_center b on b.is_deleted=0 and b.is_default=1 and b.dt_center_source_id=a.id
inner join ide.rdos_project_engine c on c.is_deleted=0 and c.project_id=b.project_id  and c.engine_type = 4
set a.schema_name = c.engine_identity
where a.is_deleted=0 and a.data_type_code =31;

--  Oracle
update dt_pub_service.dsc_info a 
INNER JOIN ide.rdos_batch_data_source_center b on b.is_deleted=0 and b.is_default=1 and b.dt_center_source_id=a.id
inner join ide.rdos_project_engine c on c.project_id=b.project_id  and c.engine_type = 5
set a.schema_name = c.engine_identity
where a.is_deleted=0 and a.data_type_code =2;

--  Greenplum
update dt_pub_service.dsc_info a 
INNER JOIN ide.rdos_batch_data_source_center b on b.is_deleted=0 and b.is_default=1 and b.dt_center_source_id=a.id
inner join ide.rdos_project_engine c on c.project_id=b.project_id  and c.engine_type = 6
set a.schema_name = c.engine_identity
where a.is_deleted=0 and a.data_type_code =36;

--  AnalyticDB for PostgreSQL
update dt_pub_service.dsc_info a 
INNER JOIN ide.rdos_batch_data_source_center b on b.is_deleted=0 and b.is_default=1 and b.dt_center_source_id=a.id
inner join ide.rdos_project_engine c on c.project_id=b.project_id  and c.engine_type = 9
set a.schema_name = c.engine_identity
where a.is_deleted=0 and a.data_type_code =54;

