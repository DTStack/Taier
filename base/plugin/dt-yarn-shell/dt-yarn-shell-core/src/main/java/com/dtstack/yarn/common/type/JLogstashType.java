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

    @Override
    public String name() {
        return "JLOGSTASH";
    }


}
