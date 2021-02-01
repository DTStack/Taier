-- 迁移记录表
INSERT INTO `alert_record`(`id`,`alert_content_id`,`tenant_id`,`app_type`,`user_id`,`read_status`,`status`,`context`,`alert_record_status`,`alert_record_send_status`,`is_deleted`,`node_address`)
SELECT
r.id,
r.content_id alert_channel_id ,
r.tenant_id,
r.app_type,
r.user_id,
r.read_status,
0,
CONCAT("{\"content\":\"",(SELECT c.content FROM dt_notify_record_content c WHERE r.content_id = c.id),"\"}" )  context,
3,
1,
r.is_deleted,
""
FROM dt_notify_record_read r
WHERE r.is_deleted = 0