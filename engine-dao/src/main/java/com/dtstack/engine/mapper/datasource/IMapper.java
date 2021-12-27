package com.dtstack.engine.mapper.datasource;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/5/18
 * @desc 基础Mapper
 */
public interface IMapper<T> extends BaseMapper<T> {

    /**
     * 模型物理删除操作（清除暂存模型数据）
     *
     * @param tab 指定表名
     * @param val 指定modelId
     */
    int clearModelStore(@Param("tab") String tab,
                        @Param("val") Long val);
}
