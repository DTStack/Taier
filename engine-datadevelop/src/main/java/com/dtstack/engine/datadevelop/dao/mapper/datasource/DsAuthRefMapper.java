package com.dtstack.pubsvc.dao.mapper.datasource;

import com.dtstack.pubsvc.dao.bo.datasource.DsAuthRefBO;
import com.dtstack.pubsvc.dao.mapper.IMapper;
import com.dtstack.pubsvc.dao.po.datasource.DsAuthRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Mapper
public interface DsAuthRefMapper extends IMapper<DsAuthRef> {

    List<DsAuthRefBO> mapDaIdName(@Param("dataInfoIds") List<Long> dataInfoIds);

    List<Long> getDataIdByAppTypes(@Param("appTypes") List<Integer> appTypes);
}
