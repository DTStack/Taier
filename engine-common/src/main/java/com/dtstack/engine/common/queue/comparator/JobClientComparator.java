package com.dtstack.engine.common.queue.comparator;

import com.dtstack.engine.common.JobClient;

import java.util.Comparator;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/8/20
 */
public class JobClientComparator implements Comparator<JobClient> {
    @Override
    public int compare(JobClient o1, JobClient o2) {
        return Long.compare(o2.getPriority(), o1.getPriority());
    }
}
