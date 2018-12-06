package com.dtstack.rdos.engine.execution.base.restart;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.ClientFactory;
import com.dtstack.rdos.engine.execution.base.IClient;
import com.dtstack.rdos.engine.execution.base.enums.EngineType;
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

public class RestartStrategyUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RestartStrategyUtil.class);

    private Map<String, IRestartStrategy> dealerMap = Maps.newHashMap();

    private Map<String, String> dealerClassNameMap = Maps.newHashMap();

    private static String DEFAULT_DEALER_CLASS_NAME = "com.dtstack.rdos.engine.execution.base.restart.DefaultRestartStrategy";

    private static RestartStrategyUtil singleton = new RestartStrategyUtil();

    private RestartStrategyUtil(){
        dealerClassNameMap.put("flink120", "com.dtstack.rdos.engine.execution.flink120.FlinkRestartStrategy");
        dealerClassNameMap.put("flink130", "com.dtstack.rdos.engine.execution.flink130.FlinkRestartStrategy");
        dealerClassNameMap.put("flink140", "com.dtstack.rdos.engine.execution.flink140.FlinkRestartStrategy");
        dealerClassNameMap.put("flink150", "com.dtstack.rdos.engine.execution.flink150.FlinkRestartStrategy");
        dealerClassNameMap.put("spark", "com.dtstack.rdos.engine.execution.spark210.SparkRestartStrategy");
        dealerClassNameMap.put("spark_yarn", "com.dtstack.rdos.engine.execution.sparkyarn.SparkRestartStrategy");
        dealerClassNameMap.put("spark_yarn_cdh", "com.dtstack.rdos.engine.execution.spark160.sparkyarn.SparkRestartStrategy");

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

    public static RestartStrategyUtil getInstance(){
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
                dealerClassName = DEFAULT_DEALER_CLASS_NAME;
            }

            try {
                IRestartStrategy dealer = Class.forName(dealerClassName, false, loader)
                        .asSubclass(IRestartStrategy.class).newInstance();
                dealerMap.put(key, dealer);
            } catch (Exception e) {
                LOG.error("", e);
                System.exit(-1);
            }
        }
    }

    public boolean retrySubmitFail(String jobId, String engineType, String msg){
        IRestartStrategy dealer = getDealer(engineType);
        if(dealer == null){
            throw new RdosException("can't find result dealer with engine type:" + engineType);
        }

        return dealer.retrySubmitFail(jobId, msg, null);
    }

    public boolean checkNOResource(String engineType, String msg){
        IRestartStrategy dealer = getDealer(engineType);
        if(dealer == null){
            throw new RdosException("can't find result dealer with engine type:" + engineType);
        }

        return dealer.checkNOResource(msg);
    }

    public boolean checkCanRestart(String jobId, String engineJobId, String engineType, IClient client){
        IRestartStrategy dealer = getDealer(engineType);
        if(dealer == null){
            throw new RdosException("can't find result dealer with engine type:" + engineType);
        }

        return dealer.checkCanRestart(jobId,engineJobId, client);
    }

    public IRestartStrategy getDealer(String engineType){
        IRestartStrategy dealer = dealerMap.get(engineType);

        if(dealer == null){
            String dealerClassName = DEFAULT_DEALER_CLASS_NAME;
            try {
                ClassLoader loader = ClientFactory.getClassLoader(engineType);
                String key = EngineType.getEngineTypeWithoutVersion(engineType);
                dealer = Class.forName(dealerClassName, false, loader)
                        .asSubclass(IRestartStrategy.class).newInstance();
                dealerMap.put(key, dealer);
            }catch (Exception e){
                LOG.error("", e);
                throw new RdosException("can't find result dealer with engine type:" + engineType);
            }
        }

        return dealer;
    }
}
