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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class HeartbeatResponse implements Writable {

  private BooleanWritable isCompleted;
  private LongWritable interResultTimeStamp;

  private static final Log LOG = LogFactory.getLog(HeartbeatResponse.class);

  public HeartbeatResponse() {
    isCompleted = new BooleanWritable(false);
    interResultTimeStamp = new LongWritable(Long.MIN_VALUE);
  }

  public HeartbeatResponse(Long timeStamp) {
    this.interResultTimeStamp = new LongWritable(timeStamp);
  }

  public HeartbeatResponse(Boolean isCompleted, Long timeStamp) {
    this.isCompleted = new BooleanWritable(isCompleted);
    this.interResultTimeStamp = new LongWritable(timeStamp);
  }

  public Boolean getIsCompleted() {
    return isCompleted.get();
  }

  public Long getInterResultTimeStamp() {
    return interResultTimeStamp.get();
  }

  @Override
  public void write(DataOutput dataOutput) {
    try {
      isCompleted.write(dataOutput);
      interResultTimeStamp.write(dataOutput);
    } catch (IOException e) {
      LOG.error("containerStatus write error: " + e);
    }
  }

  @Override
  public void readFields(DataInput dataInput) {
    try {
      isCompleted.readFields(dataInput);
      interResultTimeStamp.readFields(dataInput);
    } catch (IOException e) {
      LOG.error("containerStatus read error:" + e);
    }
  }
}
