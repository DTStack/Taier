  ALTER TABLE `rdos_stream_task_checkpoint` DROP COLUMN `checkpoint`;
  ALTER TABLE `rdos_stream_task_checkpoint` DROP COLUMN `trigger_start`;
  ALTER TABLE `rdos_stream_task_checkpoint` DROP COLUMN `trigger_end`;

  ALTER TABLE `rdos_stream_task_checkpoint` ADD COLUMN `checkpoint_id` varchar(64);
  ALTER TABLE `rdos_stream_task_checkpoint` ADD COLUMN `checkpoint_trigger` timestamp null COMMENT 'checkpoint触发时间';
  ALTER TABLE `rdos_stream_task_checkpoint` ADD COLUMN `checkpoint_savepath` varchar(128) COMMENT 'checkpoint存储路径';
  ALTER TABLE `rdos_stream_task_checkpoint` ADD UNIQUE KEY `taskid_checkpoint` (`task_id`,`checkpoint_id`) COMMENT 'taskid和checkpoint组成的唯一索引'
