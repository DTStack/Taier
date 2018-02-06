package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2018/1/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ResultMsgDealerUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ResultMsgDealerUtil.class);

    private Map<String, IResultMsgDealer> dealerMap = Maps.newHashMap();

    private Map<String, String> dealerClassNameMap = Maps.newHashMap();

    private static ResultMsgDealerUtil singleton = new ResultMsgDealerUtil();

    private ResultMsgDealerUtil(){
        dealerClassNameMap.put("flink120", "com.dtstack.rdos.engine.execution.flink120.FlinkResultMsgDealer");
        dealerClassNameMap.put("flink130", "com.dtstack.rdos.engine.execution.flink130.FlinkResultMsgDealer");
        dealerClassNameMap.put("flink140", "com.dtstack.rdos.engine.execution.flink140.FlinkResultMsgDealer");
        dealerClassNameMap.put("spark", "com.dtstack.rdos.engine.execution.spark210.SparkResultMsgDealer");
        dealerClassNameMap.put("spark_yarn", "com.dtstack.rdos.engine.execution.sparkyarn.SparkResultMsgDealer");

        List<Map<String,Object>> configList = ConfigParse.getEngineTypeList();
        List<String> typeList = Lists.newArrayList();

        configList.forEach(pluginInfo -> {
            String clientTypeStr = (String) pluginInfo.get(ConfigParse.TYPE_NAME_KEY);
            if(clientTypeStr == null){
                String errorMess = "node.yml of engineTypes setting error, typeName must not be null!!!";
                LOG.error(errorMess);
                System.exit(-1);
            }

            typeList.add(clientTypeStr);

        });

        init(typeList);
    }

    public static ResultMsgDealerUtil getInstance(){
        return singleton;
    }

    public void init(List<String> pluginTypeList){
        for(String type : pluginTypeList){
            String key = EngineType.getEngineTypeWithoutVersion(type);
            ClassLoader loader = ClientFactory.getClassLoader(type);
            if(loader == null){
                LOG.error("can't get class loader for plugin type:{}", type);
                System.exit(-1);
            }

            String dealerClassName = dealerClassNameMap.get(type);
            if(dealerClassName == null){
                LOG.error("need to init dealer className.{}", type);
                System.exit(-1);
            }

            try {
                IResultMsgDealer dealer =  Class.forName(dealerClassName).asSubclass(IResultMsgDealer.class).newInstance();
                dealerMap.put(key, dealer);
            } catch (Exception e) {
                LOG.error("", e);
                System.exit(-1);
            }
        }
    }

    public boolean checkFailureForEngineDown(String engineType, String msg){
        IResultMsgDealer dealer = dealerMap.get(engineType);
        if(dealer == null){
            throw new RdosException("can't find result dealer with engine type:" + engineType);
        }

        return dealer.checkFailureForEngineDown(msg);
    }

    public boolean checkNOResource(String engineType, String msg){
        IResultMsgDealer dealer = dealerMap.get(engineType);
        if(dealer == null){
            throw new RdosException("can't find result dealer with engine type:" + engineType);
        }

        return dealer.checkNOResource(msg);
    }
}
