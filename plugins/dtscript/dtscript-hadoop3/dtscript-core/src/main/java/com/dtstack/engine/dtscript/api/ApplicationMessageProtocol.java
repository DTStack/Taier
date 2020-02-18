package com.dtstack.engine.dtscript.api;

import com.dtstack.engine.dtscript.common.Message;
import org.apache.hadoop.ipc.VersionedProtocol;

/**
 * The Protocal between clients and ApplicationMaster to fetch Application Messages.
 */
public interface ApplicationMessageProtocol extends VersionedProtocol {

  long VERSION_ID = 1L;

  /**
   * Fetch application from ApplicationMaster.
   */
  Message[] fetchApplicationMessages();

  Message[] fetchApplicationMessages(int maxBatch);
}
