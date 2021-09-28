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

package com.dtstack.engine.dtscript.api;

import com.dtstack.engine.dtscript.common.HeartbeatRequest;
import com.dtstack.engine.dtscript.common.HeartbeatResponse;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import com.dtstack.engine.dtscript.container.DtContainerId;
import org.apache.hadoop.ipc.VersionedProtocol;

public interface ApplicationContainerProtocol extends VersionedProtocol {

  long versionID = 1090L;

  HeartbeatResponse heartbeat(DtContainerId containerId, HeartbeatRequest heartbeatRequest);

  LocalRemotePath[] getOutputLocation();

  LocalRemotePath[] getInputSplit(DtContainerId containerId);

}
