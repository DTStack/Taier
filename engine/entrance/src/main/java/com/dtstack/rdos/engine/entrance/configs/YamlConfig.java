package com.dtstack.rdos.engine.entrance.configs;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;


/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月23日 下午1:25:45
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
@SuppressWarnings("rawtypes")
public class YamlConfig implements Config{
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static Logger logger = LoggerFactory.getLogger(YamlConfig.class);

    @Override
    public Map parse(String filename){
    	try{
            Yaml yaml = new Yaml();
            if (filename.startsWith(YamlConfig.HTTP) || filename.startsWith(YamlConfig.HTTPS)) {
                URL httpUrl;
                URLConnection connection;
                httpUrl = new URL(filename);
                connection = httpUrl.openConnection();
                connection.connect();
                return (Map) yaml.load(connection.getInputStream());
            } else {
                FileInputStream input = new FileInputStream(new File(filename));
                return (Map) yaml.load(input);
            }
    	}catch(Exception e){
    		logger.error("load yaml config error", e);
    		System.exit(1);
    	}
       return null;
    }
}
