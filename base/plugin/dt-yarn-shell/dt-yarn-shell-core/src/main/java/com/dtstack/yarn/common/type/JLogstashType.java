package com.dtstack.yarn.common.type;

import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.client.ClientArguments;
import com.dtstack.yarn.util.NetUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class JLogstashType extends AppType {

    @Override
    public String buildCmd(ClientArguments clientArguments, DtYarnConfiguration conf) {

        String root = conf.get("jlogstash.root");
        if (StringUtils.isBlank(root)) {
            throw new IllegalArgumentException("Must specify jlogstash.root");
        }
        String javaHome = conf.get("java.home");
        if (StringUtils.isBlank(javaHome)) {
            throw new IllegalArgumentException("Must specify java.home");
        }
        String cmdOpts = clientArguments.getCmdOpts();
        if (StringUtils.isBlank(cmdOpts)) {
            throw new IllegalArgumentException("Must specify cmdOpts");
        }

        String encodedOpts = "";
        try {
            encodedOpts = URLEncoder.encode(clientArguments.getCmdOpts(), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("encodedOpts: " + encodedOpts);

        String cmd = javaHome + "/java -cp " + root + "/jlogstash.jar com.dtstack.jlogstash.JlogstashMain -l stdout -vvv -f " + encodedOpts + " -p " + root;

        System.out.println("my cmd: " + cmd);
        return cmd;

    }

    /**
     * logstash 需要对inputs中存在beats的情况，为port进行模式处理
     * 如果端口存在，则使用端口；
     * 不存在，则使用默认端口6767。
     * <p>
     * 端口被占用会进行检测，并递增进行重新设置端口，如6767被占用，则使用6768
     */
    @Override
    public String cmdContainerExtra(String cmd, Map<String, Object> containerInfo) {
        try {
            String[] args = cmd.split("\\s+");
            String fStr = null;
            for (int i = 0; i < args.length - 1; ++i) {
                if (args[i].equals("-f")) {
                    fStr = URLDecoder.decode(args[i + 1], "UTF-8");
                    break;
                }
            }
            if (StringUtils.isNotBlank(fStr)) {
                Map configs = objectMapper.readValue(fStr, Map.class);
                List<Map> inputs = (List<Map>) MapUtils.getObject(configs, "inputs", Collections.EMPTY_LIST);
                if (!inputs.isEmpty()) {
                    int i = 1;
                    for (Map input : inputs) {
                        Iterator<Map.Entry<String, Map>> inputIT = input.entrySet().iterator();
                        while (inputIT.hasNext()) {
                            Map.Entry<String, Map> inputEntry = inputIT.next();
                            String inputType = inputEntry.getKey();
                            Map inputConfig = inputEntry.getValue();
                            if ("Beats".equalsIgnoreCase(inputType)) {
                                int configPort = MapUtils.getInteger(inputConfig, "port", 6767);
                                int port = NetUtils.getAvailablePortRange(configPort);
                                Map<String, Object> beats = new HashMap<>(1);
                                beats.put("port", port);
                                containerInfo.put("beats" + i++, beats);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.cmdContainerExtra(cmd, containerInfo);
    }

    @Override
    public String name() {
        return "JLOGSTASH";
    }

}
