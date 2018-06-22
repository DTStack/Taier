package net.qihoo.xlearning.common;

import net.qihoo.xlearning.common.LogType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Message implements Writable {
  private net.qihoo.xlearning.common.LogType logType;
  private String message;

  public Message() {
    this.logType = net.qihoo.xlearning.common.LogType.STDERR;
    this.message = "";
  }

  public Message(net.qihoo.xlearning.common.LogType logType, String message) {
    this.logType = logType;
    this.message = message;
  }

  public net.qihoo.xlearning.common.LogType getLogType() {
    return logType;
  }

  public String getMessage() {
    return message;
  }


  @Override
  public void write(DataOutput dataOutput) throws IOException {
    WritableUtils.writeEnum(dataOutput, this.logType);
    Text.writeString(dataOutput, message);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    this.logType = WritableUtils.readEnum(dataInput, LogType.class);
    this.message = Text.readString(dataInput);
  }
}
