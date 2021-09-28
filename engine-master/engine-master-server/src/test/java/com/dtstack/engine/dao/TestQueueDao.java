/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
