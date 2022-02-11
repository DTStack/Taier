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

package com.dtstack.taier.develop.utils.develop.sync.template;

public interface JobElementPath {

    String JOB = "$.job";
    String SETTING = "$.job.setting";
    String CONTENT_ARRAY = "$.job.content";
    String CONTENT_FIRST = "$.job.content[0]";
    String READER = "$.job.content[0].reader";
    String READER_NAME = "$.job.content[0].reader.name";
    String READER_PARAMETER = "$.job.content[0].reader.parameter";
    String WRITER = "$.job.content[0].writer";
    String WRITER_NAME = "$.job.content[0].writer.name";
    String WRITER_PARAMETER = "$.job.content[0].writer.parameter";
    String SPEED = "$.job.setting.speed.bytes";
    String CHANNEL = "$.job.setting.speed.channel";
    String RECORD = "$.job.setting.errorLimit.record";
    String PERCENTAGE = "$.job.setting.errorLimit.percentage";
    String DIRTY = "$.job.setting.dirty";
    String DIRTY_PATH = "$.job.setting.dirty.path";
    String DIRTY_HADOOP_CONFIG = "$.job.setting.dirty.hadoopConfig";
}
