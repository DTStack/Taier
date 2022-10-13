package com.dtstack.taier.develop.graph.adapter;

import com.dtstack.taier.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
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
        return Objects.hash(val(), parent());
    }


    public static List<ScheduleTaskTaskShadeFlatGraphSideAdapterFlat> build(List<ScheduleTaskTaskShade> scheduleTaskTaskShades) {
        List<ScheduleTaskTaskShadeFlatGraphSideAdapterFlat> adapters = new ArrayList<>();
        scheduleTaskTaskShades.forEach(taskTaskShade -> {
            adapters.add(new ScheduleTaskTaskShadeFlatGraphSideAdapterFlat(taskTaskShade));
        });

        return adapters;
    }

}
