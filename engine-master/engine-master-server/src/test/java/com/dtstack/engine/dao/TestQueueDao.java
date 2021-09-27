package com.dtstack.engine.dao;

import com.dtstack.engine.domain.Queue;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author chener
 * @Classname TestQueueDao
 * @Description TODO
 * @Date 2020/11/25 14:57
 * @Created chener@dtstack.com
 */
public interface TestQueueDao {

    @Insert({"INSERT INTO console_queue(id,engine_id,queue_name,capacity,max_capacity,queue_state,parent_queue_id,queue_path)VALUES(#{queue.id},#{queue.engineId},#{queue.queueName},#{queue.capacity},#{queue.maxCapacity},#{queue.queueState},#{queue.parentQueueId},#{queue.queuePath})"})
    @Options(useGeneratedKeys=true, keyProperty = "queue.id", keyColumn = "id")
    Integer insert(@Param("queue") Queue queue);

    @Select({"select * from console_queue limit 1"})
    Queue getOne();

}
