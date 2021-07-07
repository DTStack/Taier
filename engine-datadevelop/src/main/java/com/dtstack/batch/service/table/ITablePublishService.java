package com.dtstack.batch.service.table;

import com.dtstack.batch.domain.BatchTableInfo;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.Tenant;

/**
 * 从测试项目发布表
 * Date: 2019/6/14
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface ITablePublishService {

    /**
     * 表发布到生产环境
     * @param sourceTableInfo
     * @param sourceProject
     * @param sourceDtUicTenantId
     * @param produceTenant
     * @param userId
     * @return 如果生产环境新生成表则返回 1, 否则 0
     * @throws Exception
     */
    Integer publish(BatchTableInfo sourceTableInfo, Project sourceProject, Long sourceDtUicTenantId, Tenant produceTenant, Long userId) throws Exception;
}
