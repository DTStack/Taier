package com.dtstack.taier.datasource.plugin.hdfs3.reader;

import com.dtstack.taier.datasource.api.dto.HdfsQueryDTO;
import org.apache.hadoop.conf.Configuration;

import java.util.List;

/**
 * hdfs读取操作
 *
 * @author luming
 * @date 2022/3/16
 */
public interface HdfsReader {
    /**
     * 根据不同文件类型读取hdfs数据
     *
     * @param configuration hadoop conf
     * @param hdfsQueryDTO  queryDto
     * @return data，如目录下存在多个文件，则会将多个文件内的数据汇总到最终集合中返回
     */
    List<String> readByType(Configuration configuration, HdfsQueryDTO hdfsQueryDTO);

    /**
     * 一次性读取text类型文件的所有数据
     *
     * @param configuration hadoop conf
     * @param hdfsPath      hdfs文件路径
     * @return data
     */
    String readText(Configuration configuration, String hdfsPath);
}
