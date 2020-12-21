package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.TenantResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 11:01 上午 2020/10/15
 */
public interface TenantResourceDao {

    /**
    * @author zyd
    * @Description 插入
    * @Date 11:02 上午 2020/10/15
    * @Param [tenantResource]
    * @retrun java.lang.Integer
    **/
    Integer insert(TenantResource tenantResource);

    /**
    * @author zyd
    * @Description 根据租户id删除
    * @Date 11:05 上午 2020/10/15
    * @Param [tenantId, dtUicTenantId]
    * @retrun java.lang.Integer
    **/
    Integer delete(@Param("tenantId") Long tenantId, @Param("dtUicTenantId") Long dtUicTenantId);

    /**
    * @author zyd
    * @Description 根据uic租户id和任务类型查找
    * @Date 11:48 上午 2020/10/15
    * @Param [dtUicTenantId, taskType]
    * @retrun com.dtstack.engine.api.domain.TenantResource
    **/
    TenantResource selectByUicTenantIdAndTaskType(@Param("dtUicTenantId") Long dtUicTenantId,@Param("taskType") Integer taskType);

    /**
    * @author zyd
    * @Description 根据uic租户id查找
    * @Date 5:42 下午 2020/10/15
    * @Param [dtUicTenantId]
    * @retrun java.util.List<com.dtstack.engine.api.domain.TenantResource>
    **/
    List<TenantResource> selectByUicTenantId(Long dtUicTenantId);
}
