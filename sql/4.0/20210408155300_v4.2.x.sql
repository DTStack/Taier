-- 修改console_component的唯一索引
DROP INDEX index_component ON `console_component`;
ALTER TABLE `console_component` ADD UNIQUE index_component ( `engine_id`, `component_type_code`, `hadoop_version` );
-- 组件默认版本
ALTER TABLE console_component ADD COLUMN `is_default` TINYINT NOT NULL DEFAULT 1 COMMENT '组件默认版本';
-- console_kerberos添加组件版本字段
ALTER TABLE console_kerberos ADD COLUMN `component_version` VARCHAR ( 25 )  COMMENT '组件版本';
-- console_kerberos表中组件版本
DROP PROCEDURE IF EXISTS addKerberosVersion ;;
DELIMITER ;;
CREATE PROCEDURE addKerberosVersion ( ) BEGIN
	DECLARE type TINYINT;
	DECLARE version VARCHAR ( 25 );
	DECLARE cluster INT ( 11 );
	DECLARE lableFlag TINYINT DEFAULT FALSE;
	DECLARE components CURSOR FOR (
		SELECT
			cc.component_type_code,
			cc.hadoop_version,
			ce.cluster_id
		FROM
			console_component cc
			LEFT JOIN console_engine ce ON ce.id = cc.engine_id
			LEFT JOIN console_cluster ccl ON ccl.id = ce.cluster_id
		);
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET lableFlag=TRUE;
OPEN components;
label:LOOP
		FETCH components INTO type,version,cluster;
		IF lableFlag THEN
			LEAVE label;
END IF;
		IF version !='' THEN
UPDATE console_kerberos ck
SET ck.component_version = version
WHERE
        ck.cluster_id = cluster
  AND ck.component_type = type;
END IF;
END LOOP label;
CLOSE components ;
COMMIT;
END;
CALL addKerberosVersion ( );
DROP PROCEDURE IF EXISTS addKerberosVersion;


-- schedule_task_shade和schedule_job版本
ALTER TABLE schedule_task_shade ADD COLUMN `component_version` VARCHAR ( 25 )  COMMENT '组件版本';
ALTER TABLE schedule_job ADD COLUMN `component_version` VARCHAR ( 25 )  COMMENT '组件版本';
DROP PROCEDURE IF EXISTS addTaskShadeVersion ;;
DELIMITER ;;
CREATE PROCEDURE addTaskShadeVersion ( )
BEGIN

DECLARE noVersion int(11) ;
DECLARE type int(11);
DECLARE component int(11);

-- 所有需要版本号的task_shade
DECLARE shadeId int(11);
DECLARE tenantId int(11);
DECLARE taskType int(11);
DECLARE taskId int(11);
DECLARE appType int(11);
DECLARE componentVersion VARCHAR(25);
DECLARE flag TINYINT DEFAULT FALSE;
DECLARE componentType int(11);

-- 游标结束
DECLARE labelFlag TINYINT DEFAULT FALSE;
-- 需要更新的数据
DECLARE taskShadeList CURSOR FOR(
		SELECT id,dtuic_tenant_id,task_type,task_id,app_type from schedule_task_shade
);
-- 无版本的
DECLARE noVersionSet CURSOR FOR (
select * FROM (
		SELECT -1
		 UNION ALL
		SELECT 10
		 UNION ALL
		SELECT 16
		 UNION ALL
		SELECT 9
		 UNION ALL
		SELECT 27
		 UNION ALL
		SELECT 26
         UNION ALL
		SELECT 13
         UNION ALL
		SELECT 14
		) a
);

-- 任务类型和版本映射
DECLARE typeComponentMap CURSOR FOR(
SELECT * FROM (
	SELECT 3,0
	UNION ALL
	SELECT 1,1
	UNION ALL
	SELECT 2,1
	UNION ALL
	SELECT 4,1
	UNION ALL
	SELECT 7,3
	UNION ALL
	SELECT 8,3
	UNION ALL
	SELECT 12,7
	UNION ALL
	SELECT 16,8
	UNION ALL
	SELECT 19,12
	UNION ALL
	SELECT 20,13
	UNION ALL
	SELECT 17,9
	UNION ALL
	SELECT 18,11
	UNION ALL
	SELECT 21,14
	UNION ALL
	SELECT 24,16
	UNION ALL
	SELECT 22,2
	UNION ALL
	SELECT 23,2
	UNION ALL
	SELECT 25,2
	UNION ALL
	SELECT 7,3
	) b
);
-- 游标结束标志
declare continue handler for not found set labelFlag = TRUE;

OPEN taskShadeList;
label1:LOOP
	FETCH taskShadeList INTO shadeId,tenantId,taskType,taskId,appType;
	-- 如果没有数据
	IF labelFlag THEN
		LEAVE label1;
END IF;
	SET flag = FALSE;
	-- 无版本判断
BEGIN
			DECLARE labelFlag2 TINYINT DEFAULT FALSE;
			DECLARE CONTINUE HANDLER FOR NOT found SET labelFlag2 = TRUE;
OPEN noVersionSet;
label2:LOOP
			FETCH noVersionSet INTO noVersion;
			IF labelFlag2 THEN
				LEAVE label2;
END IF;
			IF noVersion = taskType THEN
				SET flag = TRUE;
END IF;
END LOOP label2;
CLOSE noVersionSet;
END;
	-- 有版本
	IF flag != TRUE THEN
		SET componentType = NULL;
		-- 找到版本号
BEGIN
					DECLARE labelFlag3 TINYINT DEFAULT FALSE;
					DECLARE CONTINUE HANDLER FOR NOT found SET labelFlag3 = TRUE;
OPEN typeComponentMap;
label3:LOOP
					FETCH typeComponentMap INTO type,component;
					IF labelFlag3 THEN
						LEAVE label3;
END IF;
					IF type = taskType THEN
						SET componentType = component;
END IF;
END LOOP label3;
CLOSE typeComponentMap;
END;

		-- 查询版本
		SET componentVersion =(
			SELECT
				cc.hadoop_version
			FROM
				console_component cc
				 left JOIN console_engine ce ON ce.id = cc.engine_id
				 left JOIN console_dtuic_tenant cdt ON cdt.dt_uic_tenant_id =  tenantId
				 left join console_engine_tenant cet on cet.tenant_id = cdt.id
			where cc.component_type_code = componentType limit 1
		);
		IF componentVersion != '' THEN
			-- 更新shade版本号
UPDATE schedule_task_shade sts SET sts.component_version = componentVersion WHERE sts.id = shadeId;
-- 更新job版本号
UPDATE schedule_job sj SET sj.component_version= componentVersion where sj.task_id=taskId and sj.app_type=appType;
END IF;
END IF;
END LOOP label1;
CLOSE taskShadeList;
COMMIT;
END;
CALL addTaskShadeVersion ( );
DROP PROCEDURE IF EXISTS addTaskShadeVersion;
