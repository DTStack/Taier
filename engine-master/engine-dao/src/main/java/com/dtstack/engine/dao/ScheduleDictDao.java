package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yuebai
 * @date 2021-03-02
 */
public interface ScheduleDictDao {

    List<ScheduleDict> listDictByType(@Param("type") Integer type);

    ScheduleDict getTypeDefault(@Param("type") Integer type);

    ScheduleDict getByNameValue(@Param("type") Integer type, @Param("dictName") String dictName, @Param("dictValue") String dictValue,@Param("dependName") String dependName);

    List<ScheduleDict> getByDependName(@Param("type") Integer type,@Param("dependName") String dependName);
}
