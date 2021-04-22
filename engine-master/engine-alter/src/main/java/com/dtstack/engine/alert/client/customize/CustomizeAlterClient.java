package com.dtstack.engine.alert.client.customize;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.ICustomizeChannel;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.client.sms.AbstractSmsAlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.load.JarCache;
import com.dtstack.lang.data.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 2:10 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CustomizeAlterClient extends AbstractAlterClient {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    protected R send(AlterContext alterContext) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(alterContext.getAlertGateJson());
        String className = jsonObject.getString(ConstCustomizeAlter.CUSTOMIZE_CLASS);
        if (StringUtils.isBlank(className)) {
            throw new AlterException("Sending a custom jar must configure the full className of the jar package, please configure the field in the configuration:" + ConstCustomizeAlter.CUSTOMIZE_CLASS);
        }

        String jarPath = alterContext.getJarPath();
        if (StringUtils.isBlank(jarPath)) {
            throw new AlterException("The custom jar must be passed in the jarPath");
        }

        if (jarPath.contains(ConstCustomizeAlter.PATH_CUT)) {
            jarPath = jarPath.substring(0, jarPath.indexOf(ConstCustomizeAlter.PATH_CUT));
        }

        String content = alterContext.getContent();
        if (StringUtils.isBlank(content)) {
            throw new AlterException("Custom jar must be passed in alarm content");
        }

        Map<String, Object> evn = alterContext.getEvn();

        long startTime = System.currentTimeMillis();

        JSONObject data = new JSONObject();
        data.put("title",alterContext.getTitle());
        data.put("content",content);

        try {
            ICustomizeChannel sender = (ICustomizeChannel) JarCache.getInstance().getChannelInstance(jarPath, className);
            R r = sender.sendCustomizeAlert(data.toJSONString(),evn);
            LOGGER.info("[CustomizeAlert] end, cost={}, data={}, result={}", (System.currentTimeMillis() - startTime), data, r);
            return r;
        } catch (Exception e) {
            LOGGER.info("[CustomizeAlert] error, cost={}, data={}",(System.currentTimeMillis() - startTime), data, e);
            return R.fail("jarPath:"+jarPath +" ,loading failed, please check the configuration！");
        }
    }

    protected static class ConstCustomizeAlter {

        public static String CUSTOMIZE_CLASS = "className";

        public static String PATH_CUT = "&sftp:";

    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_DING_JAR.code();
    }

}
