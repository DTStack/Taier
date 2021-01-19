package com.dtstack.engine.alert.client.customize;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.channel.ICustomizeChannel;
import com.dtstack.engine.alert.AlterContext;
import com.dtstack.engine.alert.client.AbstractAlterClient;
import com.dtstack.engine.alert.enums.AlertGateCode;
import com.dtstack.engine.alert.exception.AlterException;
import com.dtstack.engine.alert.load.JarCache;
import com.dtstack.lang.data.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 2:10 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CustomizeAlterClient extends AbstractAlterClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected R send(AlterContext alterContext) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(alterContext.getAlertGateJson());
        String className = jsonObject.getString(ConstCustomizeAlter.CUSTOMIZE_CLASS);
        if (StringUtils.isBlank(className)) {
            throw new AlterException("发送自定义jar必须配置jar包的完整类名，请在配置中配置字段:" + ConstCustomizeAlter.CUSTOMIZE_CLASS);
        }

        String jarPath = alterContext.getJarPath();
        if (StringUtils.isBlank(jarPath)) {
            throw new AlterException("自定义jar必须传入jar路径");
        }

        String content = alterContext.getContent();
        if (StringUtils.isBlank(content)) {
            throw new AlterException("自定义jar必须传入告警内容");
        }

        long startTime = System.currentTimeMillis();

        JSONObject data = new JSONObject();
        data.put("title",alterContext.getTitle());
        data.put("content",content);

        try {
            ICustomizeChannel sender = (ICustomizeChannel) JarCache.getInstance().getChannelInstance(jarPath, className);
            R r = sender.sendCustomizeAlert(data.toJSONString(),jsonObject);
            logger.info("[CustomizeAlert] end, cost={}, data={}, result={}", (System.currentTimeMillis() - startTime), data, r);
            return r;
        } catch (Exception e) {
            logger.info("[CustomizeAlert] error, cost={}, data={}",(System.currentTimeMillis() - startTime), data, e);
            return R.fail("jarPath:"+jarPath +"加载失败，请检查配置！");
        }
    }

    protected static class ConstCustomizeAlter {

        public static String CUSTOMIZE_CLASS = "className";

    }

    @Override
    protected String getAlertGateCode() {
        return AlertGateCode.AG_GATE_DING_JAR.code();
    }

}
