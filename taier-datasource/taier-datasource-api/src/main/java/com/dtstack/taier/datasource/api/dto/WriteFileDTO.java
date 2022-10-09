package com.dtstack.taier.datasource.api.dto;

import com.dtstack.taier.datasource.api.enums.ImportDataMatchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author luming
 * @date 2022/3/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WriteFileDTO {
    /**
     * 需要读取的本地文件路径
     */
    private String localPath;
    /**
     * 要导入的字段集合
     * 举例：
     * 按名称匹配(第二个字段未选择时):
     * WriteFileDTO.builder()
     * .matchType(ImportDataMatchType.BY_NAME)
     * .importColumns(
     * Lists.newArrayList(
     * WriteFileDTO.ImportColum.builder().key("id").build(),
     * WriteFileDTO.ImportColum.builder().build(),
     * WriteFileDTO.ImportColum.builder().key("db_name").build()))
     * .tableName("test_table")
     * .localPath("/Users/edy/Downloads/mysqlData.txt")
     * .topLineIsTitle(true)
     * .build();
     * 按位置匹配:
     * WriteFileDTO.builder()
     * .matchType(ImportDataMatchType.BY_POS)
     * .importColumns(
     * Lists.newArrayList(
     * WriteFileDTO.ImportColum.builder().build(),
     * WriteFileDTO.ImportColum.builder().build(),
     * WriteFileDTO.ImportColum.builder().build()))
     * .tableName("test_table")
     * .localPath("/Users/edy/Downloads/writeByPostition.txt")
     * .topLineIsTitle(false)
     * .build();
     */
    private List<ImportColum> importColumns;
    /**
     * 字符集
     */
    @Builder.Default
    private String oriCharset = "utf-8";
    /**
     * 将写入的表名称
     */
    private String tableName;
    /**
     * 写入方式：根据name或position
     */
    private ImportDataMatchType matchType;
    /**
     * 首行是否为字段
     */
    @Builder.Default
    private Boolean topLineIsTitle = false;
    /**
     * 从文件读取数据的起始行
     */
    @Builder.Default
    private Integer startLine = 1;
    /**
     * 分隔符
     */
    @Builder.Default
    private String separator = ",";

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ImportColum {
        /**
         * 字段名称
         */
        private String key;

        private String format;

        private SimpleDateFormat dateFormat;
    }
}
