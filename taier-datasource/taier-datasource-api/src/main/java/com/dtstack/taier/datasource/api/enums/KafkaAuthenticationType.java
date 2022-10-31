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

package com.dtstack.taier.datasource.api.enums;

public enum KafkaAuthenticationType {

    SASL_PLAINTEXT(0, "PLAIN"),
    SASL_SCRAM(1, "SCRAM-SHA-256"),
    SASL_SCRAM_512(2, "SCRAM-SHA-512");
    private final int type;

    private final String mechanism;

    public int getType() {
        return type;
    }

    public String getMechanism() {
        return mechanism;
    }

    KafkaAuthenticationType(int type, String mechanism) {
        this.type = type;
        this.mechanism = mechanism;
    }
}
