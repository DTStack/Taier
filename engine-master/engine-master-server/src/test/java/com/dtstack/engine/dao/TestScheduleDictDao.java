package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleDict;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2020-07-08
 */
public interface TestScheduleDictDao {

    @Insert({" INSERT INTO schedule_dict (dict_code,dict_name,dict_value,dict_desc,type,sort,data_type) " +
            "        values(#{scheduleDict.dictCode},#{scheduleDict.dictName},#{scheduleDict.dictValue},#{scheduleDict.dictDesc},#{scheduleDict.type}," +
            "#{scheduleDict.sort},#{scheduleDict.dataType})"})
    void insert(@Param("scheduleDict") ScheduleDict scheduleDict);
}
