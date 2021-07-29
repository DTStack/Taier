package com.dtstack.batch.dao;

import com.dtstack.batch.domain.Dict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sishu.yss
 */
public interface DictDao {

    List<Dict> listByType(@Param("type") Integer type);

    List<Dict> getByTypeAndValue(@Param("type") Integer type, @Param("value") Integer value);


}
