package com.dtstack.taier.datasource.plugin.hdfs3_cdp.reader;

import com.dtstack.taier.datasource.api.dto.HdfsQueryDTO;
import com.dtstack.taier.datasource.api.enums.FileFormat;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * reader抽象类，实现公共方法和屏蔽一些接口实现
 *
 * @author luming
 * @date 2022/3/18
 */
public abstract class AbsReader implements HdfsReader {

    private static final String IMPALA_INSERT_STAGING = "_impala_insert_staging";
    /**
     * 文件行数标识，该标识用于文件/文件夹下所有子文件的行数累加，并非单个文件
     */
    protected int currentLine = 0;
    /**
     * 行列都指定的数据是否被找到。如果的话找到，后续的文件就不需要读取了
     */
    protected boolean isFound;

    @Override
    public String readText(Configuration configuration, String hdfsPath) {
        throw new SourceException("this method is not support");
    }

    @Override
    public List<String> readByType(Configuration configuration, HdfsQueryDTO hdfsQueryDTO) {
        checkParam(hdfsQueryDTO);

        List<String> results = Lists.newArrayList();
        String hdfsPath = hdfsQueryDTO.getHdfsPath();
        Path path = new Path(hdfsPath);

        try (FileSystem fs = FileSystem.get(configuration)) {
            if (!fs.exists(path)) {
                throw new SourceException("this path is not exist: " + hdfsPath);
            }
            FileStatus fStatus = fs.getFileStatus(path);
            //文件可以直接返回
            if (fStatus.isFile()) {
                results.addAll(parseFile(path, fs, configuration, hdfsQueryDTO));
            } else if (fStatus.isDirectory()) {
                FileStatus[] originalFstat = removeFile(fs, path);

                //按照路径进行排序
                List<FileStatus> sortedFstat = Arrays.stream(originalFstat)
                        .sorted(Comparator.comparing(this::convertPath))
                        .collect(Collectors.toList());

                for (FileStatus subStatus : sortedFstat) {
                    //文件夹需要判断是否进行递归读取
                    if (subStatus.isDirectory() && hdfsQueryDTO.getIsRecursion()) {
                        recurseRead(results, fs, subStatus.getPath(), hdfsQueryDTO, configuration);
                    } else if (subStatus.isFile()) {
                        results.addAll(parseFile(subStatus.getPath(), fs, configuration, hdfsQueryDTO));
                    }
                }
            }
        } catch (Exception e) {
            throw new SourceException("read hdfs error: ", e);
        }
        return results;
    }

    /**
     * 读取指定文件的具体数据
     *
     * @param path          hdfs文件绝对路径
     * @param fs            fileSystem
     * @param configuration hadoop configuration
     * @param query         param
     * @return 文件内数据
     * @throws IOException
     */
    protected abstract List<String> parseFile(
            Path path, FileSystem fs, Configuration configuration, HdfsQueryDTO query) throws IOException;

    /**
     * 文件夹递归读取
     *
     * @param results
     * @param fs
     * @param dirPath
     * @param query
     * @param configuration
     * @throws IOException
     */
    private void recurseRead(List<String> results,
                             FileSystem fs,
                             Path dirPath,
                             HdfsQueryDTO query,
                             Configuration configuration) throws IOException {
        FileStatus[] files = removeFile(fs, dirPath);

        for (FileStatus file : files) {
            if (file.isFile()) {
                results.addAll(parseFile(file.getPath(), fs, configuration, query));
                continue;
            }

            recurseRead(results, fs, file.getPath(), query, configuration);
        }
    }

    /**
     * 获取文件夹的文件状态并剔除隐藏系统文件和无关文件
     *
     * @param fs
     * @param dirPath
     * @return
     * @throws IOException
     */
    private FileStatus[] removeFile(FileSystem fs, Path dirPath) throws IOException {
        return fs.listStatus(dirPath, path ->
                !path.getName().startsWith(".") &&
                        !path.getName().startsWith("_SUCCESS") &&
                        !path.getName().startsWith(IMPALA_INSERT_STAGING) &&
                        !path.getName().startsWith("_common_metadata") &&
                        !path.getName().startsWith("_metadata"));
    }

    /**
     * 取出hdfs的绝对路径
     *
     * @param fileStatus
     * @return
     */
    protected final String convertPath(FileStatus fileStatus) {
        return fileStatus.getPath().toUri().getPath();
    }

    /**
     * 检验参数
     *
     * @param queryDTO
     */
    protected final void checkParam(HdfsQueryDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getHdfsPath())) {
            throw new SourceException("hdfs path can't be null");
        }
        if (FileFormat.TEXT.getVal().equalsIgnoreCase(queryDTO.getFileType())
                && StringUtils.isBlank(queryDTO.getSeparator())) {
            throw new SourceException("text separator can't be null");
        }
        if (queryDTO.getIsRecursion() == null) {
            queryDTO.setIsRecursion(false);
        }
        if (queryDTO.getColIndex() == null) {
            throw new SourceException("column index can't be null");
        }
    }
}
