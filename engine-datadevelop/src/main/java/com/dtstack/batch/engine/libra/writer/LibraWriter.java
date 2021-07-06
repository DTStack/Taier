package com.dtstack.batch.engine.libra.writer;

import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchTableColumn;
import com.dtstack.batch.engine.core.domain.ImportColum;
import com.dtstack.batch.enums.EImportDataMatchType;
import com.dtstack.dtcenter.loader.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2019-06-06
 */
public class LibraWriter {

	private static final Logger logger = LoggerFactory.getLogger(LibraWriter.class);

	private static int writeTemplate(String fileDirPath, String fromLineDelimiter, String oriCharSet, int startLine,
                                     boolean topLineIsTitle, List<BatchTableColumn> columnsList, Connection connection, String tableName,
                                     int pos, List<ImportColum> importColumns, EImportDataMatchType matchType) throws IOException {
		int currLineNum = 0;
		int writeLineNum = 0;
		int batchInsertSize = 100;
		int batchWriteLineSize = 0;

		PreparedStatement ps = null;
		InputStreamReader read = null;
		BufferedReader bufferedReader =null;
        FileInputStream fileInputStream = null;
		File file = checkFile(fileDirPath);
		try {
			if (CollectionUtils.isEmpty(columnsList)) {
				logger.info("{} 导入文件 列表为空", fileDirPath);
				return 0;
			}
			importColumns = importColumns.stream().filter(colum -> StringUtils.isNotEmpty(colum.getKey()))
					.collect(Collectors.toList());
			boolean canImport = (columnsList.size() >= pos
					|| (CollectionUtils.isNotEmpty(importColumns) && columnsList.size() >= importColumns.size()));

			if (!canImport) {
				logger.info("{} 导入文件列表 {} 和数据库列表{} 不一致", fileDirPath, columnsList, importColumns);
				return 0;
			}

			StringBuffer sb = new StringBuffer();
			StringBuilder beginTemplate = new StringBuilder();
            fileInputStream = new FileInputStream(file);
            read = new InputStreamReader(fileInputStream,
					StringUtils.isNotEmpty(oriCharSet) ? oriCharSet.trim() : oriCharSet);
			bufferedReader = new BufferedReader(read);
			String lineTxt = null;

			int importSize = getImportTemplate(columnsList, tableName, pos, importColumns, matchType, beginTemplate);

			sb.append(beginTemplate.toString());
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sb.toString());
			Map<Integer, Integer> mappingIndex = new HashMap<>(importSize);

			while ((lineTxt = bufferedReader.readLine()) != null) {
				currLineNum++;
				if (currLineNum == 1 && topLineIsTitle) {
					parseNameByFirstLine(fileDirPath, fromLineDelimiter, importColumns, matchType, lineTxt,
							mappingIndex);
					continue;
				}
				if (currLineNum < startLine) {
					continue;
				}
				String[] arr = lineTxt.split(fromLineDelimiter);
				if (arr.length >= pos) {
					parseColumnByLine(importSize, arr,ps);
					batchWriteLineSize++;
					writeLineNum++;
				}
				//达到行数 批量提交
				if (batchWriteLineSize == batchInsertSize){
					ps.executeBatch();
					connection.commit();
					batchWriteLineSize = 0;
				}
			}
			if (batchInsertSize != 0) {
				ps.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
			}
		} catch (Exception e) {
			logger.error("{} 文件导入失败 {}", fileDirPath, e);
			throw new RdosDefineException(String.format("第%s行数据异常,请检查", currLineNum - (topLineIsTitle ? 1 : 0)),
					ErrorCode.LIBRA_IMPORT_DATA_ERROR);
		} finally {
			DBUtil.closeDBResources(null, ps, connection);
			if (bufferedReader != null){
				bufferedReader.close();
			}
			if (read != null) {
				read.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		return writeLineNum;

	}

	/**
	 * 解析列信息
	 *
	 * @param importSize
	 * @param arr
	 * @param ps
	 */
	private static void parseColumnByLine(int importSize,String[] arr,PreparedStatement ps) {
		try {
			for (int i = 0; i < importSize; i++) {
				ps.setObject(i + 1, arr[i]);
			}
			ps.addBatch();
		} catch (SQLException e) {
			logger.error("解析列信息失败,原因是: ",e.getMessage());
		}
	}

	/**
	 * 解析第一行的 列名信息
	 *
	 * @param fileDirPath
	 * @param fromLineDelimiter
	 * @param importColumns
	 * @param matchType
	 * @param lineTxt
	 * @param mappingIndex
	 */
	private static void parseNameByFirstLine(String fileDirPath, String fromLineDelimiter,
                                             List<ImportColum> importColumns, EImportDataMatchType matchType, String lineTxt,
                                             Map<Integer, Integer> mappingIndex) {
		if (EImportDataMatchType.BY_NAME.equals(matchType)) {
			String[] split = lineTxt.split(fromLineDelimiter);
			if (split.length < importColumns.size()) {
				logger.error("{} 列数不一致", fileDirPath);
				// 第一行列数 少于提交的列数
				throw new RdosDefineException("列数 不一致", ErrorCode.LIBRA_IMPORT_DATA_ERROR);
			}
			for (int i = 0; i < split.length; i++) {
				// 第一行对应关系
				for (int j = 0; j < importColumns.size(); j++) {
					if (split[i].equalsIgnoreCase(importColumns.get(j).getKey())) {
						mappingIndex.put(j, i);
					}
				}
			}
		}
	}

	/**
	 * 插入sql前置模板信息
	 *
	 * @param columnsList
	 * @param tableName
	 * @param pos
	 * @param importColumns
	 * @param matchType
	 * @param beginTemplate
	 * @return
	 */
	private static int getImportTemplate(List<BatchTableColumn> columnsList, String tableName, int pos,
                                         List<ImportColum> importColumns, EImportDataMatchType matchType, StringBuilder beginTemplate) {
		beginTemplate.append(String.format("INSERT INTO %s (", tableName));

		int importSize = 0;
		if (EImportDataMatchType.BY_POS.equals(matchType)) {
			// 根据pos导入
			// db 取出列是 顺序 的 所以根据位置导入 一一插入即可
			importSize = pos;
		} else {
			// 根据name导入
			importSize = importColumns.size();
		}

		for (int i = 0; i < importSize; i++) {
			beginTemplate.append(columnsList.get(i).getColumnName());
			beginTemplate.append(i == importSize - 1 ? ") VALUES ( " : ",");
		}
		for (int i = 0; i < importSize; i++) {
			beginTemplate.append("?");
			beginTemplate.append(i == importSize - 1 ? ")" : ",");
		}
		return importSize;
	}

	/**
	 * 根据位置导入数据
	 *
	 * @param fileDirPath
	 * @param fromLineDelimiter
	 * @param oriCharSet
	 * @param startLine
	 * @param topLineIsTitle
	 * @param columnsList
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	public static int writeByPos(String fileDirPath, String fromLineDelimiter, String oriCharSet, int startLine,
                                 boolean topLineIsTitle, List<BatchTableColumn> columnsList, Connection connection, String tableName,
                                 int pos) throws IOException {
		return writeTemplate(fileDirPath, fromLineDelimiter, oriCharSet, startLine, topLineIsTitle, columnsList,
				connection, tableName, pos, new ArrayList<>(), EImportDataMatchType.BY_POS);
	}

	/**
	 * 根据名称匹配导入数据
	 *
	 * @param fileDirPath
	 * @param fromLineDelimiter
	 * @param oriCharSet
	 * @param startLine
	 * @param topLineIsTitle
	 * @param columnsList
	 * @param connection
	 * @param tableName
	 * @param pos
	 * @param importColums
	 * @return
	 * @throws IOException
	 */
	public static int writeByName(String fileDirPath, String fromLineDelimiter, String oriCharSet, int startLine,
                                  boolean topLineIsTitle, List<BatchTableColumn> columnsList, Connection connection, String tableName,
                                  int pos, List<ImportColum> importColums) throws IOException {
		return writeTemplate(fileDirPath, fromLineDelimiter, oriCharSet, startLine, topLineIsTitle, columnsList,
				connection, tableName, pos, importColums, EImportDataMatchType.BY_NAME);
	}

	/**
	 * 检验文件是否存在
	 *
	 * @param fromFileName
	 * @return
	 */
	private static File checkFile(String fromFileName) {
		File file = new File(fromFileName);
		if (!file.isFile() || !file.exists()) {
			logger.error(" 文件 {} 不存在", fromFileName);
			throw new RdosDefineException(String.format("文件 %s 不存在", fromFileName), ErrorCode.FILE_NOT_EXISTS);
		}
		return file;
	}

}