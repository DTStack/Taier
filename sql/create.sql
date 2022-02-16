create table console_cluster
(
	id int auto_increment
		primary key,
	cluster_name varchar(128) not null comment '集群名称',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint uk_cluster_name
		unique (cluster_name)
);

create table console_cluster_tenant
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	cluster_id int not null comment '集群id',
	queue_id int null comment '队列id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
);

create table console_component
(
	id int auto_increment
		primary key,
	component_name varchar(24) not null comment '组件名称',
	component_type_code tinyint(1) not null comment '组件类型',
	version_value varchar(25) default '' null comment '组件hadoop版本',
	upload_file_name varchar(126) default '' null comment '上传文件zip名称',
	kerberos_file_name varchar(126) default '' null comment '上传kerberos文件zip名称',
	store_type tinyint(1) default 4 null comment '组件存储类型: HDFS、NFS 默认HDFS',
	is_metadata tinyint(1) default 0 null comment '/*1 metadata*/',
	is_default tinyint(1) default 1 not null comment '组件默认版本',
	deploy_type tinyint(1) null comment '/* 0 standalone 1 yarn  */',
	cluster_id int null comment '集群id',
	version_name varchar(25) null,
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_component
		unique (cluster_id, component_type_code, version_value)
);

create table console_component_config
(
	id int auto_increment
		primary key,
	cluster_id int not null comment '集群id',
	component_id int not null comment '组件id',
	component_type_code tinyint(1) not null comment '组件类型',
	type varchar(128) not null comment '配置类型',
	required tinyint(1) not null comment 'true/false',
	`key` varchar(256) not null comment '配置键',
	value text null comment '默认配置项',
	`values` varchar(512) null comment '可配置项',
	dependencyKey varchar(256) null comment '依赖键',
	dependencyValue varchar(256) null comment '依赖值',
	`desc` varchar(512) null comment '描述',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
);

create index index_cluster_id
	on console_component_config (cluster_id);

create index index_componentId
	on console_component_config (component_id);

create table console_kerberos
(
	id bigint auto_increment
		primary key,
	cluster_id int not null comment '集群id',
	open_kerberos tinyint(1) not null comment '是否开启kerberos配置',
	name varchar(100) not null comment 'kerberos文件名称',
	remote_path varchar(200) not null comment 'sftp存储路径',
	principal varchar(50) not null comment 'principal',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	krb_name varchar(26) null comment 'krb5_conf名称',
	component_type int null comment '组件类型',
	principals text null comment 'keytab用户文件列表',
	merge_krb_content text null comment '合并后的krb5',
	component_version varchar(25) null comment '组件版本'
);

create table console_queue
(
	id int auto_increment
		primary key,
	queue_name varchar(126) not null comment '队列名称',
	capacity varchar(24) not null comment '最小容量',
	max_capacity varchar(24) not null comment '最大容量',
	queue_state varchar(24) not null comment '运行状态',
	parent_queue_id int not null comment '父队列id',
	queue_path varchar(256) not null comment '队列路径',
	cluster_id int null comment '集群id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
);

create index console_queue_cluster_id_index
	on console_queue (cluster_id);

create table datasource_classify
(
	id int(11) unsigned auto_increment comment '自增id'
		primary key,
	classify_code varchar(64) not null comment '类型栏唯一编码',
	sorted int(5) default 0 not null comment '类型栏排序字段 默认从0开始',
	classify_name varchar(64) not null comment '类型名称 包含全部和常用栏',
	is_deleted tinyint default 0 not null comment '是否删除,1删除，0未删除',
	gmt_create datetime default CURRENT_TIMESTAMP null,
	gmt_modified datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	create_user_id int default 0 null,
	modify_user_id int default 0 null,
	constraint classify_code
		unique (classify_code)
)
comment '数据源分类表';

create table datasource_form_field
(
	id int(11) unsigned auto_increment comment '自增id'
		primary key,
	name varchar(64) not null comment '表单属性名称，同一模版表单中不重复',
	label varchar(64) not null comment '属性前label名称',
	widget varchar(64) not null comment '属性格式 如Input, Radio等',
	required tinyint default 0 not null comment '是否必填 0-非必填 1-必填',
	invisible tinyint default 0 not null comment '是否为隐藏 0-否 1-隐藏',
	default_value text null comment '表单属性中默认值, 默认为空',
	place_hold text null comment '输入框placeHold, 默认为空',
	request_api varchar(256) null comment '请求数据Api接口地址，一般用于关联下拉框类型，如果不需要请求则为空',
	is_link tinyint default 0 not null comment '是否为数据源需要展示的连接信息字段。0-否; 1-是',
	valid_info text null comment '校验返回信息文案',
	tooltip text null comment '输入框后问号的提示信息',
	style text null comment '前端表单样式参数',
	regex text null comment '正则校验表达式',
	type_version varchar(64) not null comment '对应数据源版本信息',
	is_deleted tinyint default 0 not null comment '是否删除,1删除，0未删除',
	gmt_create datetime default CURRENT_TIMESTAMP null,
	gmt_modified datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	create_user_id int default 0 null,
	modify_user_id int default 0 null,
	options varchar(256) default '' null comment 'select组件下拉内容',
	constraint name
		unique (name, type_version)
)
comment '数据源表单属性表';

create table datasource_info
(
	id int(11) unsigned auto_increment comment '自增id'
		primary key,
	data_type varchar(64) not null comment '数据源类型唯一 如Mysql, Oracle, Hive',
	data_version varchar(64) null comment '数据源版本 如1.x, 0.9, 创建下的实例可能会没有版本号',
	data_name varchar(128) not null comment '数据源名称',
	data_desc text null comment '数据源描述',
	link_json text null comment '数据源连接信息, 不同数据源展示连接信息不同, 保存为json',
	data_json text null comment '数据源填写的表单信息, 保存为json, key键要与表单的name相同',
	status tinyint not null comment '连接状态 0-连接失败, 1-正常',
	is_meta tinyint default 0 not null comment '是否有meta标志 0-否 1-是',
	tenant_id int not null comment '租户主键id **可能不是id 其他唯一凭证',
	data_type_code tinyint default 0 not null comment '数据源类型编码',
	schema_name varchar(64) default '' null comment '数据源schemaName',
	is_deleted tinyint default 0 not null comment '是否删除,1删除，0未删除',
	gmt_create datetime default CURRENT_TIMESTAMP null,
	gmt_modified datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
	create_user_id int default 0 null,
	modify_user_id int default 0 null
)
comment '数据源详细信息表';

create index MODIFY_TIME
	on datasource_info (gmt_modified);

create table datasource_type
(
	id int(11) unsigned auto_increment comment '自增id'
		primary key,
	data_type varchar(64) not null comment '数据源类型唯一 如Mysql, Oracle, Hive',
	data_classify_id int not null comment '数据源分类栏主键id',
	weight decimal(20,1) default 0.0 not null comment '数据源权重',
	img_url varchar(256) null comment '数据源logo图片地址',
	sorted int(5) default 0 not null comment '数据源类型排序字段, 默认从0开始',
	invisible tinyint default 0 not null comment '是否可见',
	is_deleted tinyint default 0 not null comment '是否删除,1删除，0未删除',
	gmt_create datetime default CURRENT_TIMESTAMP null,
	gmt_modified datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	create_user_id int default 0 null,
	modify_user_id int default 0 null,
	constraint data_type
		unique (data_type)
)
comment '数据源类型信息表';

create table datasource_version
(
	id int(11) unsigned auto_increment comment '自增id'
		primary key,
	data_type varchar(64) not null comment '数据源类型唯一 如Mysql, Oracle, Hive',
	data_version varchar(64) not null comment '数据源版本 如1.x, 0.9',
	sorted int(5) default 0 not null comment '版本排序字段,高版本排序,默认从0开始',
	is_deleted tinyint default 0 not null comment '是否删除,1删除，0未删除',
	gmt_create datetime default CURRENT_TIMESTAMP null,
	gmt_modified datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	create_user_id int default 0 null,
	modify_user_id int default 0 null,
	constraint data_type
		unique (data_type, data_version)
)
comment '数据源版本表';

create table develop_catalogue
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	node_name varchar(128) not null comment '文件夹名称',
	node_pid int default -1 not null comment '父文件夹id -1:没有上级目录',
	order_val int(3) null,
	level tinyint(1) default 3 not null comment '目录层级 0:一级 1:二级 n:n+1级',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	create_user_id int not null comment '创建用户',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	catalogue_type tinyint(1) default 0 null comment '目录类型 0任务目录 1 项目目录'
)
comment '文件夹、目录表';

create index index_catologue_name
	on develop_catalogue (node_pid, node_name);

create table develop_dict
(
	id int auto_increment
		primary key,
	type int default 0 not null comment '区分字典类型，1：数据源字典 ...',
	dict_name varchar(256) default '' not null comment '字典名',
	dict_value int default 0 not null comment '字典值',
	dict_name_zh varchar(256) default '' not null comment '字典中文名',
	dict_name_en varchar(256) default '' not null comment '字典英文名',
	dict_sort int default 0 not null comment '字典顺序',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_type_dict_name
		unique (type, dict_name)
)
comment '字典表';

create table develop_function
(
	id int auto_increment
		primary key,
	name varchar(512) not null comment '函数名称',
	class_name varchar(512) null comment 'main函数类名',
	purpose varchar(1024) null comment '函数用途',
	command_formate varchar(1024) null comment '函数命令格式',
	param_desc varchar(1024) null comment '函数参数说明',
	node_pid int not null comment '父文件夹id',
	tenant_id int not null comment '租户id',
	create_user_id int not null comment '创建的用户',
	modify_user_id int not null comment '创建的用户',
	type tinyint(1) default 0 not null comment '0自定义 1系统',
	task_type int default 0 not null comment '0: SparkSQL ',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	sql_text text null comment 'sql文本'
)
comment '函数管理表';

create index index_develop_function
	on develop_function (name(128));

create table develop_function_resource
(
	id int auto_increment
		primary key,
	function_id int not null comment '函数id',
	resource_id int not null comment '对应batch资源的id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	tenant_id bigint null,
	resourceId bigint null,
	constraint index_rdos_function_resource
		unique (function_id, resource_id, is_deleted)
)
comment '函数关联的资源表';

create table develop_hive_select_sql
(
	id int auto_increment
		primary key,
	job_id varchar(256) not null comment '工作任务id',
	temp_table_name varchar(256) not null comment '临时表名',
	is_select_sql tinyint(1) default 0 not null comment '0-否 1-是',
	tenant_id int not null comment '租户id',
	user_id int null comment '执行用户',
	sql_text longtext null comment 'sql',
	parsed_columns longtext null comment '字段信息',
	task_type int null comment '任务类型',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint idx
		unique (job_id)
)
comment 'sql查询临时表';

create table develop_read_write_lock
(
	id int auto_increment
		primary key,
	lock_name varchar(256) not null comment '锁名称',
	tenant_id int null comment '租户Id',
	relation_id int not null comment 'Id',
	type varchar(256) not null comment '任务类型 ',
	create_user_id int null comment '创建人Id',
	modify_user_id int not null comment '修改的用户',
	version int default 1 not null comment '乐观锁,0是特殊含义',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_lock
		unique (relation_id, type),
	constraint index_read_write_lock
		unique (lock_name)
)
comment '读写锁记录表';

create table develop_resource
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	node_pid int not null comment '父文件夹id',
	url varchar(1028) not null comment '资源路径',
	resource_type tinyint(1) default 1 not null comment '资源类型 0:other, 1:jar, 2:py, 3:zip, 4:egg',
	resource_name varchar(256) not null comment '资源名称',
	origin_file_name varchar(256) not null comment '源文件名',
	resource_desc varchar(256) not null comment '源文描述',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	create_user_id int not null comment '新建资源的用户',
	modify_user_id int not null comment '修改人',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	node_id bigint null
)
comment '资源表';

create index index_resource_name
	on develop_resource (resource_name(128));

create index index_resource_type
	on develop_resource (resource_type, is_deleted);

create table develop_sys_parameter
(
	id int auto_increment
		primary key,
	param_name varchar(64) not null comment '参数名称',
	param_command varchar(64) not null comment '参数替换指令',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
)
comment '任务开发-系统参数表';

create table develop_task
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	node_pid int not null comment '父文件夹id',
	name varchar(256) not null comment '任务名称',
	task_type tinyint(1) not null comment '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL ',
	compute_type tinyint(1) not null comment '计算类型 0实时，1 离线',
	sql_text longtext not null comment 'sql 文本',
	task_params text not null comment '任务参数',
	schedule_conf varchar(512) not null comment '调度配置 json格式',
	period_type tinyint(2) null comment '周期类型',
	schedule_status tinyint(1) default 0 not null comment '0未开始,1正常调度,2暂停',
	submit_status tinyint(1) default 0 not null comment '0未提交,1已提交',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	modify_user_id int not null comment '最后修改task的用户',
	create_user_id int not null comment '新建task的用户',
	version int default 0 not null comment 'task版本',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	task_desc varchar(256) not null,
	main_class varchar(256) not null,
	exe_args text null,
	flow_id int default 0 not null comment '工作流id',
	component_version varchar(25) null comment '组件版本'
)
comment '任务表';

create index index_name
	on develop_task (name(128));

create table develop_task_param
(
	id int auto_increment
		primary key,
	task_id int not null comment 'batch 任务id',
	type int(2) not null comment '0:系统参数, 1:自定义参数',
	param_name varchar(64) not null comment '参数名称',
	param_command varchar(64) not null comment '参数替换指令',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
)
comment '任务开发-任务参数配置表';

create index index_batch_task_parameter
	on develop_task_param (task_id, param_name);

create table develop_task_param_shade
(
	id int auto_increment
		primary key,
	task_id int not null comment 'batch 任务id',
	type int(2) not null comment '0:系统参数, 1:自定义参数',
	param_name varchar(64) not null comment '参数名称',
	param_command varchar(64) not null comment '参数替换指令',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
)
comment '任务参数配置- 提交表';

create index index_batch_task_parameter
	on develop_task_param_shade (task_id, param_name);

create table develop_task_resource
(
	id int auto_increment
		primary key,
	task_id int not null comment 'batch 任务id',
	resource_id int null comment '对应batch资源的id',
	resource_type int null comment '使用资源的类型 1:主体资源, 2:引用资源',
	tenant_id int not null comment '租户id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_project_task_resource_id
		unique (task_id, resource_id, resource_type)
)
comment '任务和资源关联表';

create table develop_task_resource_shade
(
	id int auto_increment
		primary key,
	task_id int not null comment 'batch 任务id',
	resource_id int null comment '对应batch资源的id',
	resource_type int null comment '使用资源的类型 1:主体资源, 2:引用资源',
	tenant_id int not null comment '租户id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_project_task_resource_shade_id
		unique (task_id, resource_id, resource_type)
)
comment '任务资源关联信息- 提交表';

create table develop_task_task
(
	id int auto_increment
		primary key,
	task_id int not null comment 'batch 任务id',
	parent_task_id int null comment '对应batch任务父节点的id',
	tenant_id int not null comment '租户id',
	parent_apptype int(2) default 1 not null comment '对应任务父节点的产品类型',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_batch_task_task
		unique (parent_task_id, task_id, parent_apptype)
)
comment '任务上下游关联关系表';

create table develop_task_template
(
	id int auto_increment
		primary key,
	task_type tinyint(2) not null comment '任务类型',
	type tinyint(2) not null comment '1-ods  2-dwd  3-dws  4-ads  5-dim',
	content text not null comment '任务内容',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
)
comment '任务模板字典表';

create table develop_task_version
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	task_id int not null comment '父文件夹id',
	origin_sql longtext null comment '原始sql',
	sql_text longtext not null comment 'sql 文本',
	publish_desc text not null comment '任务参数',
	create_user_id int not null comment '新建的用户',
	version int default 0 not null comment 'task版本',
	task_params text not null comment '任务参数',
	schedule_conf varchar(512) not null comment '调度配置 json格式',
	schedule_status tinyint(1) default 0 not null comment '0未开始,1正常调度,2暂停',
	dependency_task_ids text not null comment '依赖的任务id，多个以,号隔开',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
)
comment '任务具体版本信息表';

create table develop_tenant_component
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	task_type tinyint(1) not null comment '任务类型',
	component_identity varchar(256) not null comment '组件的标识信息，也就是组件配置的dbname',
	status tinyint(1) default 0 not null comment '项目状态0：初始化，1：正常,2:禁用,3:失败',
	create_user_id int null comment '创建人id',
	modify_user_id int null comment '修改人id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
)
comment '项目与engine的关联关系表';

create table dict
(
	id int auto_increment
		primary key,
	dict_code varchar(64) not null comment '字典标识',
	dict_name varchar(64) null comment '字典名称',
	dict_value text null comment '字典值',
	dict_desc text null comment '字典描述',
	type tinyint(1) default 0 not null comment '枚举值',
	sort int default 0 not null comment '排序',
	data_type varchar(64) default 'STRING' not null comment '数据类型',
	depend_name varchar(64) default '' null comment '依赖字典名称',
	is_default tinyint(1) default 0 not null comment '是否为默认值选项',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
)
comment '通用数据字典';

create index index_dict_code
	on dict (dict_code);

create index index_type
	on dict (type);

create table schedule_engine_job_cache
(
	id int auto_increment
		primary key,
	job_id varchar(256) not null comment '任务id',
	job_name varchar(256) null comment '任务名称',
	compute_type tinyint(2) not null comment '计算类型stream/batch',
	stage tinyint(2) not null comment '处于master等待队列：1 还是exe等待队列 2',
	job_info longtext not null comment 'job信息',
	node_address varchar(256) null comment '节点地址',
	job_resource varchar(256) null comment 'job的计算引擎资源类型',
	job_priority bigint null comment '任务优先级',
	is_failover tinyint(1) default 0 not null comment '0：不是，1：由故障恢复来的任务',
	wait_reason text null comment '任务等待原因',
	tenant_id int null comment '租户id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_job_id
		unique (job_id)
);

create table schedule_engine_job_retry
(
	id int auto_increment
		primary key,
	status tinyint(1) default 0 not null comment '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
	job_id varchar(256) not null comment '离线任务id',
	engine_job_id varchar(256) null comment '离线任务计算引擎id',
	application_id varchar(256) null comment '独立运行的任务需要记录额外的id',
	exec_start_time datetime null comment '执行开始时间',
	exec_end_time datetime null comment '执行结束时间',
	retry_num int(10) default 0 not null comment '执行时，重试的次数',
	log_info mediumtext null comment '错误信息',
	engine_log longtext null comment '引擎错误信息',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	retry_task_params text null comment '重试任务参数'
);

create index idx_job_id
	on schedule_engine_job_retry (job_id)
	comment '任务实例 id';

create table schedule_fill_data_job
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	job_name varchar(64) default '' not null comment '补数据任务名称',
	run_day varchar(64) not null comment '补数据运行日期yyyy-MM-dd',
	from_day varchar(64) null comment '补数据开始业务日期yyyy-MM-dd',
	to_day varchar(64) null comment '补数据结束业务日期yyyy-MM-dd',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	create_user_id int not null comment '发起操作的用户',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	fill_data_info mediumtext null comment '补数据信息',
	fill_generate_status tinyint(2) default 0 not null comment '补数据生成状态：0默认值，按照原来的接口逻辑走。1 表示正在生成，2 完成生成补数据实例，3生成补数据失败',
	constraint index_task_id
		unique (tenant_id, job_name)
);

create table schedule_job
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	job_id varchar(256) not null comment '工作任务id',
	job_key varchar(256) default '' not null comment '工作任务key',
	job_name varchar(256) default '' not null comment '工作任务名称',
	task_id int not null comment '任务id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	create_user_id int not null comment '发起操作的用户',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	type tinyint(1) default 2 not null comment '0正常调度 1补数据 2临时运行',
	is_restart tinyint(1) default 0 not null comment '0：非重启任务, 1：重启任务',
	cyc_time varchar(64) not null comment '调度时间 yyyyMMddHHmmss',
	dependency_type tinyint(2) default 0 not null comment '依赖类型',
	flow_job_id varchar(256) default '0' not null comment '工作流实例id',
	period_type tinyint(2) null comment '周期类型',
	status tinyint(1) default 0 not null comment '任务状态 UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)',
	task_type tinyint(1) not null comment '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
	fill_id int default 0 null comment '补数据id，默认为0',
	exec_start_time datetime null comment '执行开始时间',
	exec_end_time datetime null comment '执行结束时间',
	exec_time int default 0 null comment '执行时间',
	submit_time datetime null comment '提交时间',
	max_retry_num int(10) default 0 not null comment '最大重试次数',
	retry_num int(10) default 0 not null comment '执行时，重试的次数',
	node_address varchar(64) null comment '节点地址',
	version_id int(10) default 0 null comment '任务运行时候版本号',
	next_cyc_time varchar(64) null comment '下一次调度时间 yyyyMMddHHmmss',
	engine_job_id varchar(256) null comment '离线任务计算引擎id',
	application_id varchar(256) null comment '独立运行的任务需要记录额外的id',
	compute_type tinyint(1) default 1 not null comment '计算类型STREAM(0), BATCH(1)',
	phase_status tinyint(1) default 0 not null comment '运行状态: CREATE(0):创建,JOIN_THE_TEAM(1):入队,LEAVE_THE_TEAM(2):出队',
	job_execute_order bigint default 0 not null comment '按照计算时间排序字段',
	fill_type tinyint(2) default 0 not null comment '0 默认值 周期实例，立即运行等非补数据实例的默认值 1 可执行补数据实例 2 中间实例 3 黑名单',
	submit_user_name varchar(64) null comment '提交用户名',
	constraint idx_jobKey
		unique (job_key),
	constraint index_job_id
		unique (job_id, is_deleted)
);

create index idx_cyctime
	on schedule_job (cyc_time);

create index idx_exec_start_time
	on schedule_job (exec_start_time);

create index idx_name_type
	on schedule_job (job_name(128), type);

create index index_engine_job_id
	on schedule_job (engine_job_id(128));

create index index_fill_id
	on schedule_job (fill_id);

create index index_flow_job_id
	on schedule_job (flow_job_id);

create index index_gmt_modified
	on schedule_job (gmt_modified);

create index index_job_execute_order
	on schedule_job (job_execute_order);

create index index_task_id
	on schedule_job (task_id);

create table schedule_job_expand
(
	id int auto_increment
		primary key,
	job_id varchar(256) not null comment '工作任务id',
	retry_task_params mediumtext null comment '重试任务参数',
	job_graph mediumtext null comment 'jobGraph构建json',
	job_extra_info mediumtext null comment '任务提交额外信息',
	engine_log longtext collate utf8mb4_bin null,
	log_info longtext null comment '错误信息',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_job_id
		unique (job_id)
);

create table schedule_job_graph_trigger
(
	id int auto_increment
		primary key,
	trigger_type tinyint(3) not null comment '0:正常调度 1补数据',
	trigger_time datetime not null comment '调度时间',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted int(10) default 0 not null comment '0正常 1逻辑删除',
	constraint index_trigger_time
		unique (trigger_time)
);

create table schedule_job_job
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	job_key varchar(256) not null comment 'batch 任务key',
	parent_job_key varchar(256) null comment '对应batch任务父节点的key',
	job_key_type int default 2 not null comment 'parentJobKey类型： RelyType 1. 自依赖实例key 2. 上游任务key 3. 上游任务的下一个周期key',
	rule int null comment 'parentJobKey类型： RelyType 1. 自依赖实例key 2. 上游任务key 3. 上游任务的下一个周期key',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
);

create index idx_job_jobKey
	on schedule_job_job (parent_job_key(128));

create index idx_job_parentJobKey
	on schedule_job_job (job_key(255), parent_job_key(255));

create table schedule_job_operator_record
(
	id int auto_increment
		primary key,
	job_id varchar(255) not null comment '任务id',
	version int(10) default 0 null comment '版本号',
	operator_expired datetime default CURRENT_TIMESTAMP not null comment '操作过期时间',
	operator_type tinyint(1) default 0 not null comment '操作类型 0杀死 1重跑 2 补数据',
	force_cancel_flag tinyint(1) default 0 not null comment '强制标志 0非强制 1强制',
	node_address varchar(255) null comment '节点地址',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint job_id
		unique (job_id, operator_type, is_deleted)
);

create table schedule_plugin_info
(
	id int auto_increment
		primary key,
	plugin_key varchar(255) not null comment '插件配置信息md5值',
	plugin_info text not null comment '插件信息',
	type tinyint(2) not null comment '类型 0:默认插件, 1:动态插件(暂时数据库只存动态插件)',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_plugin_id
		unique (plugin_key)
);

create table schedule_plugin_job_info
(
	id int auto_increment
		primary key,
	job_id varchar(255) not null comment '任务id',
	job_info longtext not null comment '任务信息',
	log_info text null comment '任务信息',
	status tinyint(2) not null comment '任务状态',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_job_id
		unique (job_id)
);

create index idx_gmt_modified
	on schedule_plugin_job_info (gmt_modified)
	comment '修改时间';

create table schedule_task_shade
(
	id int auto_increment
		primary key,
	tenant_id int default -1 not null comment '租户id',
	name varchar(256) default '' not null comment '任务名称',
	task_type tinyint(1) not null comment '任务类型 -1:虚节点, 0:sparksql, 1:spark, 2:数据同步, 3:pyspark, 4:R, 5:深度学习, 6:python, 7:shell, 8:机器学习, 9:hadoopMR, 10:工作流, 12:carbonSQL, 13:notebook, 14:算法实验, 15:libra sql, 16:kylin, 17:hiveSQL',
	compute_type tinyint(1) not null comment '计算类型 0实时，1 离线',
	sql_text longtext not null comment 'sql 文本',
	task_params text not null comment '任务参数',
	task_id int not null comment '任务id',
	schedule_conf varchar(512) not null comment '调度配置 json格式',
	period_type tinyint(2) null comment '周期类型',
	schedule_status tinyint(1) default 0 not null comment '0未开始,1正常调度,2暂停',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	modify_user_id int not null comment '最后修改task的用户',
	create_user_id int not null comment '新建task的用户',
	version_id int default 0 not null comment 'task版本',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	task_desc varchar(256) not null comment '任务描述',
	exe_args text null comment '额外参数',
	flow_id int default 0 not null comment '工作流id',
	component_version varchar(25) null,
	constraint index_task_id
		unique (task_id)
);

create index index_name
	on schedule_task_shade (name(128));

create table schedule_task_shade_info
(
	id int auto_increment
		primary key,
	task_id int not null comment '任务id',
	info text null comment '任务运行信息',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_task_id
		unique (task_id)
);

create table schedule_task_task_shade
(
	id int auto_increment
		primary key,
	tenant_id int not null comment '租户id',
	task_id int not null comment 'batch 任务id',
	parent_task_id int null comment '对应batch任务父节点的id',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除',
	constraint index_batch_task_task
		unique (task_id, parent_task_id)
);

create table task_param_template
(
	id bigint auto_increment
		primary key,
	task_type int default 0 null comment '任务类型',
	task_name varchar(20) null comment '任务名称',
	task_version varchar(20) null comment '任务版本',
	params text null comment '参数模版',
	gmt_create datetime default CURRENT_TIMESTAMP null,
	gmt_modified datetime default CURRENT_TIMESTAMP null,
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
);

create table tenant
(
	id int auto_increment
		primary key,
	tenant_name varchar(256) not null comment '用户名称',
	tenant_desc varchar(256) default '' null comment '租户描述',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	create_user_id int not null,
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
);

create table user
(
	id int auto_increment
		primary key,
	user_name varchar(256) not null comment '用户名称',
	password varchar(128) not null,
	phone_number varchar(256) null comment '用户手机号',
	email varchar(256) not null comment '用户手机号',
	status tinyint(1) default 0 not null comment '用户状态0：正常，1：禁用',
	gmt_create datetime default CURRENT_TIMESTAMP not null comment '新增时间',
	gmt_modified datetime default CURRENT_TIMESTAMP not null comment '修改时间',
	is_deleted tinyint(1) default 0 not null comment '0正常 1逻辑删除'
);

create index index_user_name
	on user (user_name(128));

