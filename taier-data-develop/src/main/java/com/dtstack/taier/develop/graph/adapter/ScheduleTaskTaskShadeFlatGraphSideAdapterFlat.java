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

package com.dtstack.taier.develop.graph.adapter;

import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taier.develop.graph.AbstractFlatDirectGraphSide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author leon
 * @date 2022-08-02 15:41
 **/
public class ScheduleTaskTaskShadeFlatGraphSideAdapterFlat extends AbstractFlatDirectGraphSide<Long, Long> {

    private final ScheduleTaskTaskShade ScheduleTaskTaskShade;

    public ScheduleTaskTaskShadeFlatGraphSideAdapterFlat(ScheduleTaskTaskShade ScheduleTaskTaskShade) {
        AssertUtils.notNull(ScheduleTaskTaskShade, "taskTaskShade is null");
        this.ScheduleTaskTaskShade = ScheduleTaskTaskShade;
    }

    @Override
    public Long val() {
        return ScheduleTaskTaskShade.getTaskId();
    }

    @Override
    public Long parent() {
        return ScheduleTaskTaskShade.getParentTaskId();
    }

    @Override
    public Long id() {
        return ScheduleTaskTaskShade.getTaskId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ScheduleTaskTaskShadeFlatGraphSideAdapterFlat) {
            ScheduleTaskTaskShadeFlatGraphSideAdapterFlat another = (ScheduleTaskTaskShadeFlatGraphSideAdapterFlat) obj;
            // compare taskId,parentTaskId @_@
            return (Objects.equals(this.val(), another.val())) && (Objects.equals(this.parent(), another.parent()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(val(),parent());
    }


    public static List<ScheduleTaskTaskShadeFlatGraphSideAdapterFlat> build(List<ScheduleTaskTaskShade> scheduleTaskTaskShades) {
        List<ScheduleTaskTaskShadeFlatGraphSideAdapterFlat> adapters = new ArrayList<>();
        scheduleTaskTaskShades.forEach(taskTaskShade -> {
            adapters.add(new ScheduleTaskTaskShadeFlatGraphSideAdapterFlat(taskTaskShade));
        });

        return adapters;
    }

}
