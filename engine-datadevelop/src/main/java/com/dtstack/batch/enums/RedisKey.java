package com.dtstack.batch.enums;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.common.util.RegexUtils;

import java.util.List;

/**
 * @Auther: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/7 16:11
 * @Description:
 */
public enum RedisKey {
    /**
     * 离线计算/数据开发/导入本地数据状态
     */
    IDE_DATA_DEV_IMP_LOCAL_STATUS("IDE:DATA_DEV:IMPORT_LOCAL:STATUS:T%s_U%s_P%s:%s","离线计算/数据开发/导入本地数据状态,参数:tenantId,userId,projectId,随机key"),;

    private String code;

    private String description;

    RedisKey(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getKey(Object... args) {
        List<String> lia = RegexUtils.matches(this.code, "%s");

        if (lia.size() != args.length) {
            throw new RdosDefineException("RedisKey args number wrong!");
        } else {
            return String.format(this.code, args);
        }
    }
    public byte[] getKeyBytes(Object... args) {
        List<String> lia = RegexUtils.matches(this.code, "%s");

        if (lia.size() != args.length) {
            throw new RdosDefineException("RedisKey args number wrong!");
        } else {
            return String.format(this.code, args).getBytes();
        }
    }
}
