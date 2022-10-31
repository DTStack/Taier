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

package com.dtstack.taier.datasource.api.dto.tsdb;

import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * K、V 对
 *
 * @author ：wangchuan
 * date：Created in 上午10:20 2021/6/24
 * company: www.dtstack.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue {

    private long timestamp;

    private Object value;

    public double doubleValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new SourceException("the value is " + value + " can't as double value");
    }

    public float floatValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        throw new SourceException("the value is " + value + " can't as float value");
    }

    public long longValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new SourceException("the value is " + value + " can't as long value");
    }

    public int intValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new SourceException("the value is " + value + " can't as int value");
    }


    public short shortValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        throw new SourceException("the value is " + value + " can't as short value");
    }

    public byte byteValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        throw new SourceException("the value is " + value + " can't as byte value");
    }

    public boolean boolValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        return Boolean.parseBoolean(value.toString());
    }

    public char charValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        String string = value.toString();
        return string.charAt(0);
    }

    public String stringValue() {
        if (value == null) {
            throw new SourceException("the value is null");
        }
        return value.toString();
    }

    @Override
    public String toString() {
        return "KeyValue{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
