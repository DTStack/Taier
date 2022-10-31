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

package com.dtstack.taier.script.container;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ScriptContainerId implements Writable {

  private ContainerId containerId;

  public ScriptContainerId() {
    this.containerId = null;
  }

  public ScriptContainerId(ContainerId id) {
    this.containerId = id;
  }

  public ContainerId getContainerId() {
    return containerId;
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    this.containerId = ConverterUtils.toContainerId(Text.readString(dataInput));
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    Text.writeString(dataOutput, this.toString());
  }

  @Override
  public String toString() {
    return this.containerId.toString();
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object obj) {
    ScriptContainerId other = (ScriptContainerId) obj;
    if (null == other) {
      return false;
    }
    return this.toString().equals(other.toString());
  }
}
