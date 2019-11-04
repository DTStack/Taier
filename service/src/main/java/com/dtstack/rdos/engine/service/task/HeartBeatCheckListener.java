package com.dtstack.rdos.engine.service.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.node.MasterNode;
import com.dtstack.rdos.engine.service.zk.data.BrokerHeartNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.service.db.dao.RdosNodeMachineDAO;
import com.dtstack.rdos.engine.service.enums.RdosNodeMachineType;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.google.common.collect.Maps;

/**
 * @company: www.dtstack.com
 * @author: toutian
 * @create: 2018/9/8
 */
public class HeartBeatCheckListener implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(HeartBeatCheckListener.class);

	private final static int CHECK_INTERVAL = 2000;

	public final static long STOP_HEALTH_CHECK_SEQ = -1;
	//正常停止
	private final static int RESTART_TIMEOUT_COUNT = 30;
	//宕机
	private final static int OUTAGE_TIMEOUT_COUNT = 10;
	private MasterListener masterListener;

	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	private MasterNode masterNode = MasterNode.getInstance();
	private RdosNodeMachineDAO rdosNodeMachineDAO = new RdosNodeMachineDAO();

	private int logOutput = 0;

	public HeartBeatCheckListener(MasterListener masterListener){
		this.masterListener = masterListener;
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("HeartBeatCheckListener"));
        scheduledService.scheduleWithFixedDelay(
                this,
				CHECK_INTERVAL ,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
	}

	private Map<String, BrokerNodeCount> brokerNodeCounts =  Maps.newHashMap();

	@Override
	public void run() {
        try {
            if(this.masterListener.isMaster()){
                logOutput++;
                healthCheck();
                if(PublicUtil.count(logOutput, 5)){logger.warn("HeartBeatCheckListener start check again...");}
            }
        } catch (Throwable e) {
            logger.error(ExceptionUtil.getErrorMessage(e));
        }
	}

    /**
     * 节点未正常重启、宕机都由 master 的健康检查机制来做任务恢复
	 * healthCheck 后的 broker-heart-seq = -1
     */
	private void healthCheck(){
		List<String> childrens = this.zkDistributed.getBrokersChildren();
		if(childrens!=null){
			for(String node:childrens){
				BrokerHeartNode brokerNode = this.zkDistributed.getBrokerHeartNode(node);
				boolean ignore = brokerNode == null || STOP_HEALTH_CHECK_SEQ == brokerNode.getSeq();
				if(ignore){
					continue;
				}
				BrokerNodeCount brokerNodeCount = brokerNodeCounts.computeIfAbsent(node, k->{
					this.rdosNodeMachineDAO.ableMachineNode(node, RdosNodeMachineType.SLAVE.getType());
					return new BrokerNodeCount(brokerNode);
				});
				//是否假死
				if (brokerNode.getAlive()){
					//1. 异常宕机的节点，alive=true，seq不会再进行更新
					//2. 需要加入条件 seq !=0，因为当节点重启时 seq 可能都为0，可能会满足条件 brokerNode.getAlive() && brokerNodeCount.getCount() > OUTAGE_TIMEOUT_COUNT
					//导致多执行一次 dataMigration
					if(brokerNodeCount.getHeartSeq() != 0 && brokerNodeCount.getHeartSeq() == brokerNode.getSeq().longValue()){
						brokerNodeCount.increment();
					}else{
						brokerNodeCount.reset();
					}
				}else{
					//对失去心跳的节点，可能在重启，进行计数
					brokerNodeCount.increment();
					this.rdosNodeMachineDAO.disableMachineNode(node, RdosNodeMachineType.SLAVE.getType());
				}
				//做宕机快速恢复的策略，异常宕机时alive=true
				boolean dataMigration = brokerNode.getAlive() && brokerNodeCount.getCount() > OUTAGE_TIMEOUT_COUNT ||
						brokerNodeCount.getCount() > RESTART_TIMEOUT_COUNT;
				if(dataMigration){
					//先置为 false
					this.zkDistributed.disableBrokerHeartNode(node,true);
					//再进行容灾，容灾时还需要再判断一下是否alive，node可能已经恢复
					this.masterNode.dataMigration(node);
					this.zkDistributed.removeBrokerQueueNode(node);
					this.brokerNodeCounts.remove(node);
				}else{
					brokerNodeCount.setBrokerHeartNode(brokerNode);
				}
			}
		}
	}

	private static class BrokerNodeCount{
		private AtomicLong count;
		private BrokerHeartNode brokerHeartNode;
		public BrokerNodeCount(BrokerHeartNode brokerHeartNode){
			this.count = new AtomicLong(0L);
			this.brokerHeartNode  = brokerHeartNode;
		}
		public long getCount() {
			return count.get();
		}
		public void increment() {
			count.incrementAndGet();
		}
		public void reset() {
			count.set(0L);
		}
		public long getHeartSeq() {
			return brokerHeartNode.getSeq().longValue();
		}
		public void setBrokerHeartNode(BrokerHeartNode brokerHeartNode) {
			this.brokerHeartNode = brokerHeartNode;
		}
	}
}
