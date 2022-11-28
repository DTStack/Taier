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

package com.dtstack.taier.sparkyarn.sparkyarn.file;

import com.dtstack.taier.pluginapi.CustomThreadFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ResourceCleaner implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ResourceCleaner.class);

    public static final Long INTERVAL = 24 * 60 * 60 * 1000L;

    private FileSystem fileSystem;

    private String sparkResourcesDirHostName;

    private String sparkResourcesDirMd5sum;

    private Long clearInterval;

    public static void start(
            FileSystem fileSystem,
            String sparkResourcesDirHostName,
            String sparkResourcesDirMd5sum,
            String sparkClearResourceRate) {
        ResourceCleaner cleaner = new ResourceCleaner();
        String namePrefix = cleaner.getClass().getSimpleName();
        cleaner.fileSystem = fileSystem;
        cleaner.sparkResourcesDirHostName = sparkResourcesDirHostName;
        cleaner.sparkResourcesDirMd5sum = sparkResourcesDirMd5sum;
        cleaner.clearInterval = Long.parseLong(sparkClearResourceRate) * INTERVAL;

        logger.info("ResourceCleaner Interval: {}", cleaner.clearInterval);

        ScheduledExecutorService scheduledService =
                new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(namePrefix));
        scheduledService.scheduleWithFixedDelay(cleaner, 0, INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            Path compareFile =
                    new Path(sparkResourcesDirMd5sum + SparkResourceUploader.SP + "compareFile");
            fileSystem.create(compareFile);
            FileStatus[] fileStatuses = fileSystem.listStatus(new Path(sparkResourcesDirHostName));
            for (FileStatus fileStatus : fileStatuses) {
                long fileTimeStamps = fileStatus.getModificationTime();
                long nowTime = System.currentTimeMillis();
                if (nowTime - fileTimeStamps > clearInterval) {
                    fileSystem.delete(fileStatus.getPath());
                }
            }
        } catch (IOException e) {
            logger.error("ResourcesDir Exception: ", e);
        } catch (ClassCastException e) {
            logger.error("spark.clear.resource.rate, use dataType:'int', unit:'day' ", e);
        }
    }
}
