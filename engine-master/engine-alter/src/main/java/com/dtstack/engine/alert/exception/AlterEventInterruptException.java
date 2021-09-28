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

package com.dtstack.engine.alert.exception;

import com.dtstack.engine.common.exception.BizException;

/**
 * @Auther: dazhi
 * @Date: 2021/1/26 3:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterEventInterruptException extends BizException {
    private static final long serialVersionUID = -8457636506880929633L;

    public AlterEventInterruptException(String message) {
        super(message);
    }
}
