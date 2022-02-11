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

package com.dtstack.taiga.hadoop;

import com.dtstack.taiga.hadoop.program.PackagedProgram;
import com.dtstack.taiga.pluginapi.JobParam;
import com.dtstack.taiga.pluginapi.callback.ClassLoaderCallBackMethod;
import com.dtstack.taiga.base.enums.ClassLoaderType;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapReduceTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(MapReduceTemplate.class);

    private String jobId;

    private PackagedProgram program;
    private Configuration conf;

    public MapReduceTemplate(Configuration conf, JobParam jobParam) throws Exception {
        this.conf = conf;
        initNew(jobParam);
    }

    private void initNew(JobParam jobParam) throws Exception {
        List<String> args = new ArrayList<>();
        if (StringUtils.isNotBlank(jobParam.getClassArgs())) {
            String[] argstmp = StringUtils.split(jobParam.getClassArgs(), " ");
            args.addAll(Arrays.asList(argstmp));
        }

        File jarFile = new File(jobParam.getJarPath());

        String[] array = (String[]) args.toArray(new String[args.size()]);
        this.program = new PackagedProgram(jarFile, new ArrayList<>(), ClassLoaderType.CHILD_FIRST_CACHE, jobParam.getMainClass(), array);
    }

    public String getJobId() {
        return jobId;
    }

    public void run() throws Exception {
        jobId = ClassLoaderCallBackMethod.callbackAndReset(() -> {
            return program.invokeInteractiveModeForExecution(conf);
        }, program.getUserCodeClassLoader(), true);
    }

}
