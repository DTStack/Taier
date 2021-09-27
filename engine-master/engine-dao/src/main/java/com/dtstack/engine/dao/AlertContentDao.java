package com.dtstack.engine.dao;

import com.dtstack.engine.domain.AlertContent;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther: dazhi
 * @Date: 2021/1/12 9:43 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AlertContentDao {

    Integer insert(@Param("alertContent") AlertContent alertContent);

    void updateById(@Param("alertContent") AlertContent alertContent);

    AlertContent selectById(@Param("contentId") Long contentId);
}
