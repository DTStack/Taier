package com.dtstack.engine.entrance.configs;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.dtstack.engine.common.exception.EngineAgumentsException;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月23日 下午1:25:45
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class YamlConfig implements Config{
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private static String configFilePath = System.getProperty("user.dir")+"/conf/node.yml";


    @SuppressWarnings("unchecked")
    public Map<String,Object> loadConf() throws Exception{
        Map<String,Object> nodeConfig = new YamlConfig().parse(configFilePath,Map.class);
        checkEngineAguments(nodeConfig);
        return nodeConfig;
    }

    public void checkEngineAguments(Map<String,Object> nodeConfig) throws EngineAgumentsException {
        String nodeZkAddress = (String)nodeConfig.get("nodeZkAddress");
        if(StringUtils.isBlank(nodeZkAddress)){
            throw new EngineAgumentsException("nodeZkAddress");
        }

    }

    @Override
    public <T> T parse(String filename,Class<T> classType) throws Exception{
            Yaml yaml = new Yaml();
            if (filename.startsWith(YamlConfig.HTTP) || filename.startsWith(YamlConfig.HTTPS)) {
                URL httpUrl;
                URLConnection connection;
                httpUrl = new URL(filename);
                connection = httpUrl.openConnection();
                connection.connect();
                return yaml.loadAs(connection.getInputStream(),classType);
            } else {
                FileInputStream input = new FileInputStream(new File(filename));
                return  yaml.loadAs(input,classType);
            }
    }
}
