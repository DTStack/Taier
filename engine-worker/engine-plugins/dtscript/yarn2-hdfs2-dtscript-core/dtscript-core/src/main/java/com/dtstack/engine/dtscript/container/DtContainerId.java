package com.dtstack.engine.dtscript.container;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DtContainerId implements Writable {

  private ContainerId containerId;

  public DtContainerId() {
    this.containerId = null;
  }

  public DtContainerId(ContainerId id) {
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
    DtContainerId other = (DtContainerId) obj;
    if (null == other) {
      return false;
    }
    return this.toString().equals(other.toString());
  }
}
