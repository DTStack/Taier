package com.dtstack.engine.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2020/10/9 9:48 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum ActionType {

    LOG_OUT(1, "退出登录", "退出登录"),
    COME_IN(2, "进入产品", "进入：%s"),
    ADD_USER(3, "添加用户", "添加用户：%s"),
    REMOVE_USER(4, "移除用户", "移除用户：%s"),
    CHANGE_MODE(5, "修改角色", "修改%s角色，修改前：%s，修改后：%s"),


    API_APPLY(6, "API申请", "申请者：%s , 调用次数: %s , 调用周期： %s "),
    API_AUTH(7, "API审批", "申请者：%s , 调用次数: %s , 调用周期： %s ,结果：%s "),

    API_CANCEL_REPLY_AUTH(8, "API变更授权", "%s ,申请者：%s , 调用次数: %s , 调用周期： %s "),
    //    API_REPLY_AUTH(9,"API恢复授权","申请者：%s , 调用次数: %s , 调用周期： %s "),
    API_ALTER_AUTH(10, "API编辑授权", "申请者：%s , 调用次数: %s , 调用周期： %s "),

    //    API_CREATE(11,"生成API",""),
//    API_ENROLL(12,"注册API",""),
//    API_ALTER(14,"编辑API",""),
    API_CREATE_UPDATE_ENROLL(11, "创建/编辑API", "%s"),
    API_RELEASE(13, "发布API", ""),
    API_PROHIBITION(15, "禁用API", ""),
    API_DELETE(16, "删除API", ""),

    API_DISABLE_ENABLE(17, "停用/启用API", "%s , 申请者：%s , 调用次数: %s , 调用周期： %s"),

    API_INTO(18, "进入API", ""),

    API_RESET_TOKEN(19, "重置api的token", "%s , 申请者：%s 重置token"),
    DATA_PREVIEW(20, "数据预览", "数据源类型：%s , 数据源名称：%s , 连接信息：%s "),
    PACKAGE_CREATE(21, "任务打包", "发布包名称：%s"),
    PACKAGE_DOWNLOAD(22, "下载发布包", "发布包名称：%s"),
    PACKAGE_DELETE(23, "删除发布包", "发布包名称：%s"),
    PACKAGE_UPLOAD(24, "上传发布包", "发布包名称：%s"),
    RESOURCE_UPLOAD(25, "上传资源", "编辑%s，上传资源：%s"),
    RESOURCE_DOWNLOAD(26, "下载资源", "编辑%s，下载资源：%s"),
    RESOURCE_DELETE(27, "删除资源", "编辑%s，删除资源：%s")
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
        Map<Integer, String> map = new HashMap<>();
        map.put(ActionType.LOG_OUT.getCode(), ActionType.LOG_OUT.getComment());
        map.put(ActionType.COME_IN.getCode(), ActionType.COME_IN.getComment());
        map.put(ActionType.ADD_USER.getCode(), ActionType.ADD_USER.getComment());
        map.put(ActionType.REMOVE_USER.getCode(), ActionType.REMOVE_USER.getComment());
        map.put(ActionType.CHANGE_MODE.getCode(), ActionType.CHANGE_MODE.getComment());
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

    public static Map<Integer, String> getCommonMap() {
        Map<Integer, String> map = new HashMap();
        map.put(LOG_OUT.getCode(), LOG_OUT.getComment());
        map.put(COME_IN.getCode(), COME_IN.getComment());
        map.put(ADD_USER.getCode(), ADD_USER.getComment());
        map.put(REMOVE_USER.getCode(), REMOVE_USER.getComment());
        map.put(CHANGE_MODE.getCode(), CHANGE_MODE.getComment());
        return map;
    }

    public static Map<Integer, String> getStreamMap() {
        Map<Integer, String> map = getCommonMap();
        map.put(DATA_PREVIEW.getCode(), DATA_PREVIEW.getComment());
        map.put(PACKAGE_CREATE.getCode(), PACKAGE_CREATE.getComment());
        map.put(PACKAGE_DOWNLOAD.getCode(), PACKAGE_DOWNLOAD.getComment());
        map.put(PACKAGE_DELETE.getCode(), PACKAGE_DELETE.getComment());
        map.put(PACKAGE_UPLOAD.getCode(), PACKAGE_UPLOAD.getComment());
        map.put(RESOURCE_UPLOAD.getCode(), RESOURCE_UPLOAD.getComment());
        map.put(RESOURCE_DOWNLOAD.getCode(), RESOURCE_DOWNLOAD.getComment());
        map.put(RESOURCE_DELETE.getCode(), RESOURCE_DELETE.getComment());
        return map;
    }

}
