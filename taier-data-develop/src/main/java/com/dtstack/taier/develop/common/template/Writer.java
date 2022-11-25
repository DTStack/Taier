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

package com.dtstack.taier.develop.common.template;

import com.alibaba.fastjson.JSONObject;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public interface Writer extends CheckFormat {
    JSONObject toWriterJson();

    String toWriterJsonString();

    /**
     * Whether the current write data source needs to rewrite the write mode,
     * and regenerate the required mode through the replace and insert passed in from the front end
     *  For example: the front end of the FTP writer passes in replace and insert,
     *  but chunjun needs overwrite and append, so it needs to be rewritten
     *  default is not rewritten
     *
     * @return If you return True, you need to rewrite the write Mode,
     *          and if you return False, you don't need to rewrite the write Mode
     */
    default boolean resetWriteMode() {
        return false;
    }
}
