package com.dtstack.taiga.scheduler.server.action.fill;

import com.dtstack.taiga.scheduler.enums.FillDataTypeEnum;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:43 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface FillDataTask {

    /**
     * 设置补数据类型
     *
     * @param fillDataType
     * @return
     */
    FillDataTypeEnum setFillDataType(Integer fillDataType);

    /**
     * 获取运行集合 R集合
     *
     * @return R集合
     */
    Set<Long> getRunList();

    /**
     * 填充集合
     *
     * @param run 需要跑的节点
     * @return all 集合
     */
    Set<Long> getAllList(Set<Long> run);

}
