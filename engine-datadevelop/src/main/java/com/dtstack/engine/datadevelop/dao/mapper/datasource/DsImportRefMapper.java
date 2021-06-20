package com.dtstack.pubsvc.dao.mapper.datasource;

import com.dtstack.pubsvc.dao.mapper.IMapper;
import com.dtstack.pubsvc.dao.po.datasource.DsImportRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Mapper
public interface DsImportRefMapper extends IMapper<DsImportRef> {

    /**
     * 查询引入表判断是否是迁移的数据源
     * @param dsInfoId
     * @return
     */
    List<DsImportRef> getImportDsByInfoId(Long dsInfoId);
}
