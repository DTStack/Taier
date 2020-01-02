package com.dtstack.engine.dtscript.common;

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
