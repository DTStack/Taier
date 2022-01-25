/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taiga.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.common.template.Writer;
import com.dtstack.taiga.develop.utils.develop.sync.job.PluginName;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jingzhen
 */
public class HBaseWriter extends HBaseBase implements Writer {

    private static final Pattern PATTERN = Pattern.compile("\\$\\((.*?)\\)(_?)");

    private String nullMode;
    private long writeBufferSize;
    private String rowkey = "";
    private List<String> srcColumns;

    public String getNullMode() {
        return nullMode;
    }

    public void setNullMode(String nullMode) {
        this.nullMode = nullMode;
    }

    public long getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(long writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public List<String> getSrcColumns() {
        return srcColumns;
    }

    public void setSrcColumns(List<String> srcColumns) {
        this.srcColumns = srcColumns;
    }

    @Override
    public JSONObject toWriterJson() {
        JSONObject parameter = new JSONObject(true);
        parameter.put("hbaseConfig", this.getHbaseConfig());

        //转换rowkeyColumn
        parameter.put("rowkeyColumn", this.checkRowkey());
        parameter.put("table", this.getTable());
        parameter.put("nullMode", this.getNullMode());
        parameter.put("writeBufferSize", this.getWriteBufferSize());

        List<JSONObject> cols = new ArrayList<>();
        for(JSONObject col : this.getColumn()) {
            JSONObject newCol = new JSONObject();
            if ("rowkey".equals(col.get("key"))) {
               newCol.put("name", "rowkey");
            } else {
                newCol.put("name", col.get("cf") + ":" + col.get("key"));
            }

            newCol.put("type", col.get("type"));
            cols.add(newCol);
        }
        parameter.put("column", cols);
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());
        parameter.put("sftpConf", this.getSftpConf());
        parameter.put("remoteDir", this.getRemoteDir());

        JSONObject write = new JSONObject(true);

        write.put("name", PluginName.HBase_W);
        write.put("parameter", parameter);
        return write;
    }

    @Override
    public String toWriterJsonString() {
        return toWriterJson().toJSONString();
    }

    private String checkRowkey(){
        Matcher matcher = PATTERN.matcher(rowkey);
        Boolean isMatcher = false;
        while (matcher.find()) {
            isMatcher = true;
            String varName = matcher.group(1);
            try {
                Integer index = srcColumns.indexOf(varName.split(":")[1]);
                if(index == -1) {
                    throw new RdosDefineException("rowkey column not found: " + varName);
                }
            } catch (Exception e) {
                if (e instanceof RdosDefineException){
                    throw e;
                }else {
                    throw new RdosDefineException(String.format("请检查rowkey格式，原因是：%s", e.getMessage()));
                }
            }
        }
        if (!isMatcher){
            throw new RdosDefineException("请输入正确的rowkey格式");
        }

        return this.rowkey;
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
