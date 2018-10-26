package com.dtstack.yarn.common.type;

import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.client.ClientArguments;
import org.apache.commons.lang.StringUtils;

import java.net.URLEncoder;


public class JLogstashType extends AppType {


    @Override
    public String buildCmd(ClientArguments clientArguments, DtYarnConfiguration conf) {

        String root = conf.get("jlogstash.root");
        if(StringUtils.isBlank(root)) {
            throw new IllegalArgumentException("Must specify jlogstash.root");
        }
        String javaHome = conf.get("java.home");
        if(StringUtils.isBlank(javaHome)) {
            throw new IllegalArgumentException("Must specify java.home");
        }
        String cmdOpts = clientArguments.getCmdOpts();
        if(StringUtils.isBlank(cmdOpts)) {
            throw new IllegalArgumentException("Must specify cmdOpts");
        }

        String encodedOpts = "";
        try {
            encodedOpts = URLEncoder.encode(clientArguments.getCmdOpts(), "UTF-8");
        } catch(Exception e) {
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
     *
     * 端口被占用会进行检测，并递增进行重新设置端口，如6767被占用，则使用6768
     */
    @Override
    public String setCmdExtra(String cmd) {
        return super.setCmdExtra(cmd);
    }

    @Override
    public String name() {
        return "JLOGSTASH";
    }


}
