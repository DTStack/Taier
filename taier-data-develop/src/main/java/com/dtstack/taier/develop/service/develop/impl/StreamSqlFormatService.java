package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.develop.service.console.TenantService;
import com.dtstack.taier.develop.service.datasource.impl.KerberosService;
import com.dtstack.taier.develop.sql.utils.SqlFormatUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class StreamSqlFormatService {

    private static final String ADD_JAR_FORMAT = "ADD JAR WITH %s;";

    private static final String ADD_JAR_WITH_MAIN_FORMAT = "ADD JAR WITH %s AS %s;";

    private static final String ADD_FILE_FORMAT = "ADD FILE WITH %s;";

    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\s*([0-9a-zA-Z-_]+)\\s*\\(");

    private static final String ADD_FILE_RENAME_FORMAT = "ADD FILE WITH %s RENAME %s;";


    @Autowired
    private TenantService tenantService;

    @Autowired
    private KerberosService kerberosService;

    /**
     * 获取sql中包含的方法名称
     *
     * @param sql
     * @return
     */
    public Set<String> getFuncName(String sql) {

        if (Strings.isNullOrEmpty(sql)) {
            return Sets.newHashSet();
        }
        sql = SqlFormatUtil.formatSql(sql);

        Set<String> funcSet = Sets.newHashSet();
        Matcher matcher = FUNCTION_PATTERN.matcher(sql);
        while (matcher.find()) {
            String funcName = matcher.group(1);
            funcSet.add(funcName.toUpperCase());
        }

        return funcSet;
    }

    public String generateAddFileSQL(String path) {
        return String.format(ADD_FILE_FORMAT, path.replaceAll("//*", "/"));
    }

    /**
     * 生成添加文件的 SQL 并进行文件重命名
     *
     * @param path       文件路径
     * @param targetName 目标文件名
     * @return 生成的 SQL
     */
    public String generateAddFileRenameSQL(String path, String targetName) {
        return String.format(ADD_FILE_RENAME_FORMAT, path.replaceAll("//*", "/"), targetName);
    }
}
