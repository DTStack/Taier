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

package com.dtstack.taier.pluginapi.exception;

import com.dtstack.taier.pluginapi.io.UnsafeStringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

/**
 * Date: 2016年11月31日 下午1:26:07
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class ExceptionUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);

    /**
     * 获取错误的堆栈信息
     *
     * @param e throwable
     * @return 堆栈信息
     */
    public static String getErrorMessage(Throwable e) {
        UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static String stackTrack() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        StringBuffer mBuffer = new StringBuffer();
        mBuffer.append(System.getProperty("line.separator"));

        for (StackTraceElement e : st) {
            if (mBuffer.length() > 0) {
                mBuffer.append("  ");
                mBuffer.append(System.getProperty("line.separator"));
            }
            mBuffer.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , e.getClassName()
                    , e.getMethodName()
                    , e.getLineNumber()));
        }
        return mBuffer.toString();
    }
}
