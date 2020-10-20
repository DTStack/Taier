package com.dtstack.engine.alert.domian;

import com.dtstack.engine.alert.enums.AGgateType;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *     告警参数
 * </p>
 * @author 青涯
 */
public class AlertEvent {

    /**
     * <p>
     *     短信告警手机号码
     * </p>
     */
    private List<String> phones;

    private List<String> emails;
    /**
     * <p>
     *     附件
     * </p>
     */
    private List<File> attachFiles;

    /**
     * <p>
     *     发送邮件主题
     * </p>
     */
    private String subject;

    private List<String> dings;

    private String message;

    private AGgateType aGgateType;

    /**
     * <p>
     *     动态占位符参数
     * </p>
     */
    private Map<String,String> dynamicParams;

    private String source;
    
    /**
     * 扩展配置
     */
    private Map<String, Object> extCfg;

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getDings() {
        return dings;
    }

    public void setDings(List<String> dings) {
        this.dings = dings;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, String> getDynamicParams() {
        return dynamicParams;
    }

    public void setDynamicParams(Map<String, String> dynamicParams) {
        this.dynamicParams = dynamicParams;
    }

    public List<File> getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(List<File> attachFiles) {
        this.attachFiles = attachFiles;
    }

	public Map<String, Object> getExtCfg() {
		return extCfg;
	}

	public void setExtCfg(Map<String, Object> extCfg) {
		this.extCfg = extCfg;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

    public AGgateType getaGgateType() {
        return aGgateType;
    }

    public void setaGgateType(AGgateType aGgateType) {
        this.aGgateType = aGgateType;
    }
}
