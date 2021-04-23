package com.dtstack.engine.lineage.sourcekey;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.enums.DataSourceType;

/**
 * @Author: ZYD
 * Date: 2021/2/3 17:51
 * Description: 集群模式数据源sourceKey生成器
 * @since 1.0.0
 */
public class ClusterSourceKeyGenerator extends AbstractSourceKeyGenerator{

    public static final String PHOENIX = "phoenix";

    @Override
    public String generateSourceKey(String jdbcUrl,Integer sourceType) {

        StringBuilder sourceKey = null;
        jdbcUrl = jdbcUrl.trim();
        try {
            sourceKey = new StringBuilder();
            // Hbase和phoenix的有可能是集群模式，需要特殊处理
            if(DataSourceType.getSourceType(sourceType).equals(DataSourceType.Phoenix)
                    || DataSourceType.getSourceType(sourceType).equals(DataSourceType.PHOENIX5) ){
                jdbcUrl = jdbcUrl.substring(jdbcUrl.indexOf(PHOENIX) + PHOENIX.length()+1,
                        jdbcUrl.contains("/") ? jdbcUrl.indexOf("/") : jdbcUrl.length());
            }else{
                //是Hbase数据源，可能有两种形式ip,ip,ip:port   ip:port,ip:port,ip:port
                String[] split = jdbcUrl.split(ConfigConstant.COLON);
                if(split.length>2){
                    String[] ipPorts = jdbcUrl.split(ConfigConstant.COMMA);
                    for (String ipPort : ipPorts) {
                        sourceKey.append(ipPort.replace(ConfigConstant.COLON,"#")).append(ConfigConstant.SPLIT);
                    }
                    return sourceKey.substring(0, sourceKey.length() - 1);
                }
            }
            String[] split = jdbcUrl.split(ConfigConstant.COLON);
            String ipStr = split[0];
            String port = split[1];
            String[] ipList = ipStr.split(ConfigConstant.COMMA);
            for (String ip : ipList) {
                sourceKey.append(ip).append("#").append(port).append(ConfigConstant.SPLIT);
            }
            return sourceKey.substring(0, sourceKey.length() - 1);
        } catch (Exception e) {
            throw new RdosDefineException("jdbcUrl format is fault,jdbcUrl: "+jdbcUrl);
        }
    }
}
