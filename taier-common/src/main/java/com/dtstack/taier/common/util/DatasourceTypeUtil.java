package com.dtstack.taier.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * datasource type util
 *
 * @author ：wangchuan
 * date：Created in 15:50 2022/10/8
 * company: www.dtstack.com
 */
public class DatasourceTypeUtil {

    private static final Map<String, Integer> COMPONENT_VERSION_TYPE_MAP = Maps.newHashMap();

    private static final String MAPPING_FILE_NAME = "datasource-mapping.json";

    private static final String DATASOURCE_TYPE = "datasource-type";

    private static final String COMPONENT_TYPE = "component-type";

    private static final String COMPONENT_VERSION_NAME = "component-version-name";

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceTypeUtil.class);

    static {
        try {
            String datasourceMapping = getDatasourceMapping();
            JSONArray mappingArray = JSONArray.parseArray(datasourceMapping);
            for (int i = 0; i < mappingArray.size(); i++) {
                JSONObject mappingJson = mappingArray.getJSONObject(i);
                if (Objects.isNull(mappingJson)) {
                    continue;
                }
                Integer sourceType = mappingJson.getInteger(DATASOURCE_TYPE);
                Integer componentType = mappingJson.getInteger(COMPONENT_TYPE);
                String componentVersionName = mappingJson.getString(COMPONENT_VERSION_NAME);
                if (Objects.isNull(sourceType) || Objects.isNull(componentType)) {
                    LOGGER.warn("datasource-type or component-type is empty : {}", mappingJson.toJSONString());
                    continue;
                }
                String componentKey = getComponentKey(componentType, componentVersionName);
                COMPONENT_VERSION_TYPE_MAP.put(componentKey, sourceType);
            }
        } catch (Throwable e) {
            LOGGER.error("init datasource mapping error", e);
        }
    }

    public static Integer getTypeByComponentAndVersion(Integer componentType, String versionName) {
        if (MapUtils.isEmpty(COMPONENT_VERSION_TYPE_MAP)) {
            throw new DtCenterDefException("datasource mapping is empty.");
        }
        Integer sourceType = COMPONENT_VERSION_TYPE_MAP.get(getComponentKey(componentType, versionName));
        if (Objects.isNull(sourceType)) {
            throw new DtCenterDefException(String.format("no corresponding datasource mapping found," +
                    " componentType: %s, versionName: %s", componentType, versionName));
        }
        return sourceType;
    }

    /**
     * 根据文件名称获取 mapping 文件内容, 优先读取部署路径下 conf 文件夹下的脚本
     *
     * @return mapping 内容
     */
    private static String getDatasourceMapping() {
        // 脚本不存时到的错误信息
        Supplier<String> errMsgSupplier = () -> String.format("datasource mapping 文件: [%s] 不存在或内容为空", MAPPING_FILE_NAME);
        String mappingPath = System.getProperty("user.dir") + File.separator + "conf";

        File localMappingFile = new File(mappingPath + File.separator + MAPPING_FILE_NAME);
        if (!localMappingFile.exists()) {
            // sync copy
            synchronized (MAPPING_FILE_NAME) {
                if (!localMappingFile.exists()) {
                    URL mappingResourceUrl = DatasourceTypeUtil.class.getResource(File.separator + MAPPING_FILE_NAME);
                    if (Objects.isNull(mappingResourceUrl)) {
                        throw new RdosDefineException(errMsgSupplier.get());
                    }
                    // 写出一份到本地临时目录
                    try {
                        String tmpDir = System.getProperty("user.dir") + File.separator + "tmp";
                        FileUtil.mkdirsIfNotExist(tmpDir);
                        localMappingFile = new File(tmpDir + File.separator + MAPPING_FILE_NAME);
                        FileUtils.copyURLToFile(mappingResourceUrl, localMappingFile);
                    } catch (IOException e) {
                        throw new DtCenterDefException("写出文件到本地失败", e);
                    }
                }
            }
        }
        if (!localMappingFile.exists()) {
            throw new RdosDefineException(errMsgSupplier.get());
        }
        String mappingContent;
        try {
            mappingContent = FileUtils.readFileToString(localMappingFile);
        } catch (IOException e) {
            throw new DtCenterDefException(String.format("读取本地文件: [%s]失败", localMappingFile.getAbsolutePath()), e);
        }
        if (StringUtils.isBlank(mappingContent)) {
            throw new RdosDefineException(errMsgSupplier.get());
        }
        return mappingContent;
    }

    private static String getComponentKey(Integer componentType, String componentVersionName) {
        if (Objects.isNull(componentType)) {
            throw new DtCenterDefException("component type can't be null.");
        }
        return StringUtils.isBlank(componentVersionName) ?
                componentType + "" : String.format("%s-%s", componentType, componentVersionName);
    }
}
