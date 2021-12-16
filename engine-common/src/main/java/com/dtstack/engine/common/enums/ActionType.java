package com.dtstack.engine.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/16 12:11 上午
 * Description: No Description
 */
public enum ActionType {

    /**
     * 退出登录
     */
    LOG_OUT(1, "退出登录", "退出登录"),

    /**
     * 进入产品
     */
    COME_IN(2, "进入产品", "进入：%s"),

    /**
     * 添加用户
     */
    ADD_USER(3, "添加用户", "添加用户：%s"),

    /**
     * 移除用户
     */
    REMOVE_USER(4, "移除用户", "移除用户：%s"),

    /**
     * 修改角色
     */
    CHANGE_MODE(5, "修改角色", "修改%s角色，修改前：%s，修改后：%s"),


    /**
     * 申请api
     */
    API_APPLY(6, "API申请", "申请者：%s , 调用次数: %s , 调用周期： %s "),

    /**
     * 审批api
     */
    API_AUTH(7, "API审批", "申请者：%s , 调用次数: %s , 调用周期： %s ,结果：%s "),

    /**
     * api变更授权
     */
    API_CANCEL_REPLY_AUTH(8, "API变更授权", "%s ,申请者：%s , 调用次数: %s , 调用周期： %s "),

    /**
     * api编辑授权
     */
    API_ALTER_AUTH(10, "API编辑授权", "申请者：%s , 调用次数: %s , 调用周期： %s "),

    /**
     * 创建/编辑api
     */
    API_CREATE_UPDATE_ENROLL(11, "创建/编辑API", "%s"),

    /**
     * 发布api
     */
    API_RELEASE(13, "发布API", ""),

    /**
     * 禁用api
     */
    API_PROHIBITION(15, "禁用API", ""),

    /**
     * 删除api
     */
    API_DELETE(16, "删除API", ""),

    /**
     * 停用/启用api
     */
    API_DISABLE_ENABLE(17, "停用/启用API", "%s , 申请者：%s , 调用次数: %s , 调用周期： %s"),

    /**
     * 进入api
     */
    API_INTO(18, "进入API", ""),

    /**
     * 重置api token
     */
    API_RESET_TOKEN(19, "重置api的token", "%s , 申请者：%s 重置token"),

    /**
     * 数据预览
     */
    DATA_PREVIEW(20, "数据预览", "数据源类型：%s , 数据源名称：%s , 连接信息：%s "),

    /**
     * 任务打包
     */
    PACKAGE_CREATE(21, "任务打包", "发布包名称：%s"),

    /**
     * 下载发布包
     */
    PACKAGE_DOWNLOAD(22, "下载发布包", "发布包名称：%s"),

    /**
     * 删除发布包
     */
    PACKAGE_DELETE(23, "删除发布包", "发布包名称：%s"),

    /**
     * 上传发布包
     */
    PACKAGE_UPLOAD(24, "上传发布包", "发布包名称：%s"),

    /**
     * 上传资源
     */
    RESOURCE_UPLOAD(25, "上传资源", "编辑%s，上传资源：%s"),

    /**
     * 下载资源
     */
    RESOURCE_DOWNLOAD(26, "下载资源", "编辑%s，下载资源：%s"),

    /**
     * 删除资源
     */
    RESOURCE_DELETE(27, "删除资源", "编辑%s，删除资源：%s"),
    ;


    private int code;

    private String comment;

    private String template;

    ActionType(int code, String comment, String template) {
        this.code = code;
        this.comment = comment;
        this.template = template;
    }

    public int getCode() {
        return code;
    }

    public String getComment() {
        return comment;
    }

    public String getTemplate() {
        return template;
    }

    public static Map<Integer, String> getBatchMap() {
        return getCommonMap();
    }

    public static Map<Integer, String> getCommonMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(ActionType.LOG_OUT.getCode(), ActionType.LOG_OUT.getComment());
        map.put(ActionType.COME_IN.getCode(), ActionType.COME_IN.getComment());
        map.put(ActionType.ADD_USER.getCode(), ActionType.ADD_USER.getComment());
        map.put(ActionType.REMOVE_USER.getCode(), ActionType.REMOVE_USER.getComment());
        map.put(ActionType.CHANGE_MODE.getCode(), ActionType.CHANGE_MODE.getComment());
        return map;
    }

    public static Map<Integer, String> getStreamMap() {
        Map<Integer, String> map = getCommonMap();
        map.put(ActionType.DATA_PREVIEW.getCode(), ActionType.DATA_PREVIEW.getComment());
        map.put(ActionType.PACKAGE_CREATE.getCode(), ActionType.PACKAGE_CREATE.getComment());
        map.put(ActionType.PACKAGE_DOWNLOAD.getCode(), ActionType.PACKAGE_DOWNLOAD.getComment());
        map.put(ActionType.PACKAGE_DELETE.getCode(), ActionType.PACKAGE_DELETE.getComment());
        map.put(ActionType.PACKAGE_UPLOAD.getCode(), ActionType.PACKAGE_UPLOAD.getComment());
        map.put(ActionType.RESOURCE_UPLOAD.getCode(), ActionType.RESOURCE_UPLOAD.getComment());
        map.put(ActionType.RESOURCE_DOWNLOAD.getCode(), ActionType.RESOURCE_DOWNLOAD.getComment());
        map.put(ActionType.RESOURCE_DELETE.getCode(), ActionType.RESOURCE_DELETE.getComment());
        return map;
    }

    public static Map<Integer, String> getApiMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(ActionType.LOG_OUT.getCode(), ActionType.LOG_OUT.getComment());
        map.put(ActionType.ADD_USER.getCode(), ActionType.ADD_USER.getComment());
        map.put(ActionType.CHANGE_MODE.getCode(), ActionType.CHANGE_MODE.getComment());
        for (ActionType e : ActionType.values()) {
            if (e.toString().startsWith("API_")) {
                map.put(e.getCode(), e.getComment());
            }
        }
        return map;
    }

    public static String getCommentByCode(Integer code) {
        for (ActionType e : ActionType.values()) {
            if (code.equals(e.getCode())) {
                return e.comment;
            }
        }
        return "";
    }
}
