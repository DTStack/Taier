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

package com.dtstack.engine.common.util;

import java.util.Collection;
import java.util.Random;

public class RandomUtils {
    private static Random random;


    //双重校验锁获取一个Random单例
    public static synchronized Random getRandom() {
        if (random == null) {
                random = new Random();
            }
        return random;
    }

    public static int getRandomInt(int max) {
        return Math.abs(getRandom().nextInt()) % max;
    }

    public static <V> V getRandomValueFromMap(Collection<V> list) {
        if (list.isEmpty()) {
            return null;
        }
        int rn = getRandomInt(list.size());
        int i = 0;
        for (V value : list) {
            if (i == rn) {
                return value;
            }
            i++;
        }
        return null;
    }
}
