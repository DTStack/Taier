package com.dtstack.engine.datasource.dao.mapper.datasource;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsImportRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Mapper
public interface DsImportRefMapper extends BaseMapper<DsImportRef> {

    /**
     * 查询引入表判断是否是迁移的数据源
     * @param dsInfoId
     * @return
     */
    List<DsImportRef> getImportDsByInfoId(Long dsInfoId);
}
