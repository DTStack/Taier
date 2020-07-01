package com.dtstack.engine.dao;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TestEngineUniqueSignDao {
    @Select({"select id from schedule_engine_unique_sign where unique_sign = #{uniqueSign}"})
    List<Long> getIdByUniqueSign(String uniqueSign);
}
