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

package com.dtstack.taier.scheduler.jobdealer.bo;

import com.dtstack.taier.pluginapi.JobIdentifier;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class JobCompletedInfo implements Delayed {

    private String jobId;
    private JobIdentifier jobIdentifier;
    private int computeType;
    private long expired;

    public JobCompletedInfo(String jobId, JobIdentifier jobIdentifier, int computeType, long delay){
        this.jobId = jobId;
        this.jobIdentifier = jobIdentifier;
        this.computeType = computeType;
        this.expired = System.currentTimeMillis() + delay;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }


    public int getComputeType() {
        return computeType;
    }

    public void setComputeType(int computeType) {
        this.computeType = computeType;
    }

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(JobIdentifier jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expired - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
