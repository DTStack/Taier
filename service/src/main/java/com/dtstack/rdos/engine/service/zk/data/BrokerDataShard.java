package com.dtstack.rdos.engine.service.zk.data;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class BrokerDataShard {

    /**FIXME 如果存储的key格式发生改变--需要修改 BrokerDataTreeMap 的构造函数*/
	private BrokerDataTreeMap metas;

	public BrokerDataTreeMap getMetas() {
		return metas;
	}

	public void setMetas(BrokerDataTreeMap metas) {
		this.metas = metas;
	}
	
	public static void copy(BrokerDataShard source, BrokerDataShard target, boolean isCover){
    	if(source.getMetas()!=null){
    		if(isCover){
        		target.setMetas(source.getMetas());
    		}else{
    			target.getMetas().putAll(source.getMetas());
    		}
    	}
	}
	
	public static BrokerDataShard initBrokerDataShard(){
		BrokerDataShard brokerNode = new BrokerDataShard();
		brokerNode.setMetas(new BrokerDataTreeMap());
		return brokerNode;
	}
	
	public static BrokerDataShard initNullBrokerShard(){
		BrokerDataShard brokerDataShard = new BrokerDataShard();
		return brokerDataShard;
	}
}
