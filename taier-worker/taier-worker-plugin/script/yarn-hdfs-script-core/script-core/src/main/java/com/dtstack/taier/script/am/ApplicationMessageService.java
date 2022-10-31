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

package com.dtstack.taier.script.am;

import com.dtstack.taier.script.api.ApplicationContext;
import com.dtstack.taier.script.api.ApplicationMessageProtocol;
import com.dtstack.taier.script.common.Message;
import com.dtstack.taier.script.common.SecurityUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.service.AbstractService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;


public class ApplicationMessageService extends AbstractService implements
        ApplicationMessageProtocol {

  private static final Log LOG = LogFactory.getLog(ApplicationMessageService.class);

  private final ApplicationContext applicationContext;

  private InetSocketAddress serverAddress;

  public ApplicationMessageService(ApplicationContext applicationContext, Configuration conf) {
    super(ApplicationMessageService.class.getSimpleName());
    this.setConfig(conf);
    this.applicationContext = applicationContext;
  }

  @Override
  public void start() {
    LOG.info("Starting application message server");

    Configuration conf = SecurityUtil.disableSecureRpc(getConfig());
    RPC.Builder builder = new RPC.Builder(conf);
    builder.setProtocol(ApplicationMessageProtocol.class);
    builder.setInstance(this);
    builder.setBindAddress("0.0.0.0");
    builder.setPort(0);
    Server server;
    try {
      server = builder.build();
    } catch (Exception e) {
      LOG.error("Error starting message server!", e);
      return;
    }
    server.start();

    serverAddress = NetUtils.getConnectAddress(server);
    LOG.info("Started application message server at " + serverAddress);
  }

  @Override
  public Message[] fetchApplicationMessages() {
    int defaultMaxBatch = 100;
    return fetchApplicationMessages(defaultMaxBatch);
  }

  @Override
  public Message[] fetchApplicationMessages(int maxBatch) {
    BlockingQueue<Message> msgs = applicationContext.getMessageQueue();
    ArrayList<Message> result = new ArrayList<>();
    int count = 0;
    while (count < maxBatch) {
      Message line = msgs.poll();
      if (null == line) {
        break;
      }
      result.add(line);
      count++;
    }

    if (result.size() == 0) {
      return null;
    }
    Message[] resultArray = new Message[result.size()];
    result.toArray(resultArray);
    return resultArray;
  }

  public InetSocketAddress getServerAddress() {
    return serverAddress;
  }

  @Override
  public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
    return versionID;
  }

  @Override
  public ProtocolSignature getProtocolSignature(String protocol,
                                                long clientVersion, int clientMethodsHash) throws IOException {
    return ProtocolSignature.getProtocolSignature(this, protocol,
        clientVersion, clientMethodsHash);
  }

}
