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

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LocalRemotePath implements Writable {
  private String localLocation;

  private String dfsLocation;

  public LocalRemotePath() {
  }

  public String getLocalLocation() {
    return localLocation;
  }

  public void setLocalLocation(String localLocation) {
    this.localLocation = localLocation;
  }

  public String getDfsLocation() {
    return dfsLocation;
  }

  public void setDfsLocation(String dfsLocation) {
    this.dfsLocation = dfsLocation;
  }

  @Override
  public String toString() {
    return dfsLocation;
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    Text.writeString(dataOutput, localLocation);
    Text.writeString(dataOutput, dfsLocation);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    this.localLocation = Text.readString(dataInput);
    this.dfsLocation = Text.readString(dataInput);
  }
}
