//package com.dtstack.engine.common.config;
//
//import com.dtstack.engine.common.exception.EngineAgumentsException;
//import org.yaml.snakeyaml.Yaml;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.Map;
//
//
///**
// *
// * Date: 2017年02月23日 下午1:25:45
// * Company: www.dtstack.com
// *
// * @author sishu.yss
// */
//public abstract class AbstractYamlConfig implements Config {
//    private static final String HTTP = "http://";
//    private static final String HTTPS = "https://";
//
//    public abstract String getConfigFilePath();
//
//    @SuppressWarnings("unchecked")
//    public Map<String, Object> loadConf() throws Exception {
//        Map<String, Object> nodeConfig = this.parse(getConfigFilePath(), Map.class);
//        checkEngineArguments(nodeConfig);
//        return nodeConfig;
//    }
//
//    public void checkEngineArguments(Map<String, Object> nodeConfig) throws EngineAgumentsException {
//    }
//
//    @Override
//    public <T> T parse(String filename, Class<T> classType) throws Exception {
//        Yaml yaml = new Yaml();
//        if (filename.startsWith(AbstractYamlConfig.HTTP) || filename.startsWith(AbstractYamlConfig.HTTPS)) {
//            URL httpUrl;
//            URLConnection connection;
//            httpUrl = new URL(filename);
//            connection = httpUrl.openConnection();
//            connection.connect();
//            return yaml.loadAs(connection.getInputStream(), classType);
//        } else {
//            FileInputStream input = new FileInputStream(new File(filename));
//            return yaml.loadAs(input, classType);
//        }
//    }
//}
