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

package com.dtstack.taier.script.common;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class HeartbeatRequest implements Writable {
  private ContainerStatus xlearningContainerStatus;
  private BooleanWritable interResultSavedStatus;
  private String progressLog;
  private String containersStartTime;
  private String containersFinishTime;
  private String containerUserDir;
  private String errMsg;

  public HeartbeatRequest() {
    xlearningContainerStatus = ContainerStatus.UNDEFINED;
    interResultSavedStatus = new BooleanWritable(false);
    progressLog = "";
    errMsg = "";
    containersStartTime = "";
    containersFinishTime = "";
    containerUserDir = "";
  }

  public void setXlearningContainerStatus(ContainerStatus xlearningContainerStatus) {
    this.xlearningContainerStatus = xlearningContainerStatus;
  }

  public ContainerStatus getXlearningContainerStatus() {
    return this.xlearningContainerStatus;
  }

  public void setInnerModelSavedStatus(Boolean savedStatus) {
    this.interResultSavedStatus.set(savedStatus);
  }

  public Boolean getInnerModelSavedStatus() {
    return this.interResultSavedStatus.get();
  }

  public void setProgressLog(String xlearningProgress) {
    this.progressLog = xlearningProgress;
  }

  public String getProgressLog() {
    return this.progressLog;
  }

  public void setContainersStartTime(String startTime) {
    this.containersStartTime = startTime;
  }

  public String getContainersStartTime() {
    return this.containersStartTime;
  }

  public void setContainersFinishTime(String finishTime) {
    this.containersFinishTime = finishTime;
  }

  public String getContainersFinishTime() {
    return this.containersFinishTime;
  }

  public String getContainerUserDir() {
    return containerUserDir;
  }

  public void setContainerUserDir(String containerUserDir) {
    this.containerUserDir = containerUserDir;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    WritableUtils.writeEnum(dataOutput, this.xlearningContainerStatus);
    interResultSavedStatus.write(dataOutput);
    Text.writeString(dataOutput, this.progressLog);
    Text.writeString(dataOutput, this.errMsg);
    Text.writeString(dataOutput, this.containersStartTime);
    Text.writeString(dataOutput, this.containersFinishTime);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    this.xlearningContainerStatus = WritableUtils.readEnum(dataInput, ContainerStatus.class);
    interResultSavedStatus.readFields(dataInput);
    this.progressLog = Text.readString(dataInput);
    this.errMsg = Text.readString(dataInput);
    this.containersStartTime = Text.readString(dataInput);
    this.containersFinishTime = Text.readString(dataInput);
  }

}
