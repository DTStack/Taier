/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleTaskShade;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Date: 2020/6/21
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public interface TestScheduleTaskShadeDao {

	@Insert({"INSERT INTO schedule_task_shade (tenant_id,project_id,node_pid,name,task_type,engine_type,compute_type,sql_text,task_params,\n" +
		"schedule_conf,period_type,schedule_status,submit_status,gmt_create,gmt_modified,modify_user_id,create_user_id,owner_user_id,\n" +
		"version_id,is_deleted,task_desc,main_class,exe_args,flow_id,app_type,dtuic_tenant_id,task_id,project_schedule_status,component_version) \n" +
		"VALUES (#{scheduleTaskShade.tenantId},#{scheduleTaskShade.projectId},#{scheduleTaskShade.nodePid},#{scheduleTaskShade.name},#{scheduleTaskShade.taskType},#{scheduleTaskShade.engineType},#{scheduleTaskShade.computeType},#{scheduleTaskShade.sqlText},#{scheduleTaskShade.taskParams},\n" +
		"#{scheduleTaskShade.scheduleConf},#{scheduleTaskShade.periodType},#{scheduleTaskShade.scheduleStatus},#{scheduleTaskShade.submitStatus},#{scheduleTaskShade.gmtCreate},#{scheduleTaskShade.gmtModified},#{scheduleTaskShade.modifyUserId},#{scheduleTaskShade.createUserId},\n" +
		"#{scheduleTaskShade.ownerUserId},#{scheduleTaskShade.versionId},#{scheduleTaskShade.isDeleted},#{scheduleTaskShade.taskDesc},#{scheduleTaskShade.mainClass},#{scheduleTaskShade.exeArgs},#{scheduleTaskShade.flowId},#{scheduleTaskShade.appType},#{scheduleTaskShade.dtuicTenantId},#{scheduleTaskShade.taskId},\n" +
        "#{scheduleTaskShade.projectScheduleStatus},#{scheduleTaskShade.componentVersion})"})
	@Options(useGeneratedKeys=true, keyProperty = "scheduleTaskShade.id", keyColumn = "id")
	void insert(@Param("scheduleTaskShade") ScheduleTaskShade scheduleTaskShade);
}
