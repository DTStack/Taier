package com.dtstack.engine.entrance;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.common.util.SystemPropertyUtil;
import com.dtstack.rdos.engine.entrance.configs.YamlConfig;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestThread {

    public static void main(String[] args) throws Exception {
        SystemPropertyUtil.setSystemUserDir();
        Map<String, Object> nodeConfig = new YamlConfig().loadConf();
        ConfigParse.setConfigs(nodeConfig);
        ZkDistributed zkDistributed = ZkDistributed.createZkDistributed(nodeConfig);

        CountDownLatch countDownLatch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<InterProcessMutex> mutexList = new ArrayList<>(2);
        executorService.submit(() -> {
            try {
                InterProcessMutex mutex1 = zkDistributed.createBrokerDataShardLock("zhaojiemo_lock");
                InterProcessMutex mutex2 = zkDistributed.createBrokerDataShardLock("toutian_lock");
                mutexList.add(mutex1);
                mutexList.add(mutex2);
                mutex1.acquire();
                mutex2.acquire();
                countDownLatch.countDown();
                try {
                    System.out.println(Thread.currentThread());
//                    if (mutex2.acquire(2, TimeUnit.SECONDS)) {
                        System.out.println("thread 1 get mutex lock");
//                    } else {
//                        System.out.println("thread 1 unget mutex lock");
//                    }
//                    countDownLatch.await();//不让线程死掉
                    System.out.println("thread 1 线程退出");
                } finally {
//                    if (mutex2.isAcquiredInThisProcess()) {
//                        mutex2.release();
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        executorService.execute(() -> {
            try {
//                InterProcessMutex mutex2 = zkDistributed.createBrokerDataShardLock("toutian_lock");
//                InterProcessMutex mutex = zkDistributed.createBrokerDataShardLock("zhaojiemo_lock");
//                mutex.acquire();
//                countDownLatch.countDown();
//                countDownLatch.await();
//                if (mutex.isAcquiredInThisProcess()){
//                    System.out.println(Thread.currentThread().getId());
//                }
                try {
                    System.out.println(Thread.currentThread());

//                    if (mutex2.acquire(2, TimeUnit.SECONDS)) {
//                        System.out.println("thread 2 get mutex2 lock");
//                    } else {
//                        System.out.println("thread 2 unget mutex2 lock");
//                    }
                    for (InterProcessMutex mutex:mutexList){
                        System.out.println(mutex);
                        if (mutex.isAcquiredInThisProcess()){
                            System.out.println("thread 2 unget mutex lock");
                        }
                    }

                } finally {
                    for (InterProcessMutex mutex:mutexList){
                        if (mutex.isAcquiredInThisProcess()){
                            mutex.release();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        System.out.println("11111111111");
    }

}
