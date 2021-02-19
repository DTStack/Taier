ALTER TABLE schedule_task_commit DROP INDEX index_job_id;
CREATE unique index `index_job_id`ON schedule_task_commit  (`commit_id`,`is_deleted`,`task_id`) USING BTREE;
