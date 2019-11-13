package com.dtstack.engine.entrance;

import com.dtstack.engine.common.config.ConfigParse;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import com.dtstack.engine.entrance.configs.YamlConfig;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.service.zk.ZkDistributed;
import com.dtstack.engine.service.zk.data.BrokerDataTreeMap;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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




        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        CountDownLatch latch3 = new CountDownLatch(2);
        BrokerDataTreeMap<String, Byte> map = BrokerDataTreeMap.initBrokerDataTreeMap();
        map.put("1", new Integer(3).byteValue());
        map.put("2", new Integer(3).byteValue());
        map.put("3", new Integer(3).byteValue());

        ExecutorService submitExecutor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("submitDealer"));
        submitExecutor.submit(() -> {
            try {
                latch1.await();
                map.remove("2");
                latch2.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }finally {
                System.out.println("adsadsad");
                latch3.countDown();
            }
        });

        submitExecutor.submit(() -> {
            try {
                int i = 0;
                Set<Map.Entry<String, Byte>> entries = map.entrySet();
                for (Map.Entry<String, Byte> entry : entries) {
                    if (i == 1) {
                        latch1.countDown();
                        latch2.await();
                    }
                    System.out.println(entry.getKey() + "+" + entry.getValue());
                    i++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                latch3.countDown();
            }
        });
        latch3.await();
        System.out.println(map);
    }

}
