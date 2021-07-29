DROP PROCEDURE IF EXISTS pr_auth_admin;

DELIMITER //

create procedure pr_auth_admin()
BEGIN
DECLARE authId INT;
DECLARE authCode VARCHAR(128);
DECLARE authPId INT;
DECLARE stop INT DEFAULT 0;
DECLARE cur CURSOR FOR (select id ,code ,parent_id from rdos_permission where is_deleted=0 and type = 1);
DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET stop = null;

OPEN cur;

    FETCH cur INTO authId,authCode,authPId;
    WHILE ( stop is not null) DO
    SET @p_count = (select count(1) p_count from rdos_permission where parent_id = authId and is_deleted=0);
    IF @p_count=0 THEN
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('1', authId);
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('2', authId);
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('3', authId);
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('4', authId);
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('5', authId);
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('6', authId);
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('8', authId);
    INSERT INTO rdos_role_permission(role_id,permission_id) VALUES ('9', authId);
    END IF;
    FETCH cur INTO authId,authCode,authPId;
    END WHILE;

CLOSE cur;
END; //

DELIMITER ;

CALL pr_auth_admin();

-- 项目管理员
UPDATE rdos_role_permission SET is_deleted=1 where role_id = (select min(id) from rdos_role WHERE role_value = 3) and permission_id IN (
  SELECT id from rdos_permission where type = 1 and code in (
    'project_edit'
  )
);

-- 访客
UPDATE rdos_role_permission SET is_deleted=1 where role_id = (select min(id) from rdos_role WHERE role_value = 4) and permission_id IN (
  SELECT id from rdos_permission where type = 1 and code in (
    'project_edit',
    'project_configure_edit',
    'project_member_edit',
    'project_role_edit',
    'batchintegration_batch_edit',
    'batchintegration_batch_dbsync',
    'datadevelop_batch_taskmanager_edit',
    'datadevelop_batch_taskmanager_publish',
    'datadevelop_batch_functionmanager',
    'datadevelop_batch_resourcemanager',
    'datadevelop_batch_scriptmanager_edit',
    'datamanager_tablemanager_edit',
    'datamanager_tablemanager_editcharge',
    'datamanager_catalogue_edit',
    'datamanager_permissionmanager_query',
    'datamanager_permissionmanager_edit',
    'datamanager_permissionmanager_apply',
    'maintenance_batch_taskop',
    'maintenance_batch_scheduleop',
    'maintenance_batchtaskmanager_filldata',
    'maintenance_batchtaskmanager_query',
    'maintenance_alarm_custom_batch_edit',
    'datamodel_manager_query',
    'datamodel_manager_edit',
    'test_produce',
    'test_produce_binding_project',
    'test_produce_edit_schedule_status',
    'test_produce_edit_package',
    'test_produce_publish_package',
    'datamask_rule_edit',
    'datamask_config_edit'
  )
);

-- 运维
UPDATE rdos_role_permission SET is_deleted=1 where role_id = (select min(id) from rdos_role WHERE role_value = 5) and permission_id IN (
  SELECT id from rdos_permission where type = 1 and code in (
    'project_edit',
    'project_configure_edit',
    'project_member_edit',
    'project_role_edit',
    'test_produce_edit_schedule_status',
    'test_produce_binding_project',
    'datadevelop_batch_taskmanager_edit',
    'datadevelop_batch_functionmanager',
    'datadevelop_batch_resourcemanager',
    'datadevelop_batch_scriptmanager',
    'datamanager_tablemanager_edit',
    'datamanager_catalogue_edit',
    'datamanager_tablemanager_editcharge',
    'datamask_rule_edit',
    'datamask_config_edit',
    'batchintegration_batch_dbsync',
    'batchintegration_batch_edit'
  )
);

-- 数据开发
UPDATE rdos_role_permission SET is_deleted=1 where role_id = (select min(id) from rdos_role WHERE role_value = 6) and permission_id IN (
  SELECT id from rdos_permission where type = 1 and code in (
    'project_edit',
    'project_configure_edit',
    'project_member_edit',
    'project_role_edit',
    'test_produce_edit_schedule_status',
    'datamanager_tablemanager_editcharge',
    'datamanager_catalogue_edit',
    'test_produce_publish_package',
    'test_produce_binding_project',
    'datamask_rule_edit',
    'datamask_config_edit',
    'batchintegration_batch_edit'
  )
);

-- 删除运维编辑脚本权限
update rdos_role_permission set is_deleted = 1
where role_id in (select id from rdos_role where role_name = '运维')
  and permission_id = (SELECT id from rdos_permission where code = 'datadevelop_batch_scriptmanager_edit');