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

package com.dtstack.engine.dtscript.common;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputInfo implements Writable {

  private String aliasName;

  private List<Path> paths = new ArrayList<>();

  public InputInfo() {
  }

  public String getAliasName() {
    return aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }

  public List<Path> getPaths() {
    return paths;
  }

  public void setPaths(List<Path> paths) {
    this.paths = paths;
  }

  public void addPath(Path path) {
    paths.add(path);
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    Text.writeString(dataOutput, aliasName);
    dataOutput.writeInt(paths.size());
    for (Path p : paths) {
      Text.writeString(dataOutput, p.toString());
    }
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    this.aliasName = Text.readString(dataInput);
    this.paths = new ArrayList<>();
    int size = dataInput.readInt();
    for (int i = 0; i < size; i++) {
      this.paths.add(new Path(Text.readString(dataInput)));
    }
  }
}
