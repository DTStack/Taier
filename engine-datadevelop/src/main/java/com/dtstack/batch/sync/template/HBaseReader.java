package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.sync.job.PluginName;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jingzhen
 */
public class HBaseReader extends HBaseBase implements Reader {

    private String startRowkey = "";
    private String endRowkey = "";
    private boolean isBinaryRowkey = false;
    private int scanCacheSize = 256;
    private int scanBatchSize = 100;

    public String getStartRowkey() {
        return startRowkey;
    }

    public void setStartRowkey(String startRowkey) {
        this.startRowkey = startRowkey;
    }

    public String getEndRowkey() {
        return endRowkey;
    }

    public void setEndRowkey(String endRowkey) {
        this.endRowkey = endRowkey;
    }

    public boolean isBinaryRowkey() {
        return isBinaryRowkey;
    }

    public void setBinaryRowkey(boolean binaryRowkey) {
        isBinaryRowkey = binaryRowkey;
    }

    public int getScanCacheSize() {
        return scanCacheSize;
    }

    public void setScanCacheSize(int scanCacheSize) {
        this.scanCacheSize = scanCacheSize;
    }

    public int getScanBatchSize() {
        return scanBatchSize;
    }

    public void setScanBatchSize(int scanBatchSize) {
        this.scanBatchSize = scanBatchSize;
    }

    @Override
    public JSONObject toReaderJson() {
        JSONObject parameter = new JSONObject(true);
        JSONObject reader = new JSONObject(true);

        List<JSONObject> cols = new ArrayList<>();

        if(this.getColumn() != null) {
            for(JSONObject column : this.getColumn()) {
                JSONObject col = new JSONObject();

                if(column.containsKey("key") && column.containsKey("cf") && !column.containsKey("value")) {
                    if("rowkey".equals(column.get("key"))) {
                        col.put("name", "rowkey");
                    } else {
                        col.put("name", column.get("cf") + ":" + column.get("key"));
                    }
                }

                if(column.containsKey("type")) {
                    col.put("type", column.get("type"));
                }

                if(column.containsKey("value")) {
                    col.put("value", column.get("value"));
                }

                cols.add(col);
            }
        }

        JSONObject range = new JSONObject(true);
        range.put("startRowkey", this.getStartRowkey());
        range.put("endRowkey", this.getEndRowkey());
        range.put("isBinaryRowkey", this.isBinaryRowkey());

        parameter.put("hbaseConfig", this.getHbaseConfig());
        parameter.put("column", cols);
        parameter.put("encoding", this.getEncoding());
        parameter.put("table", this.getTable());
        parameter.put("range", range);

        parameter.put("scanCacheSize", this.getScanCacheSize());
        parameter.put("scanBatchSize", this.getScanBatchSize());
        parameter.put("sourceIds",getSourceIds());
        parameter.putAll(super.getExtralConfigMap());
        parameter.put("sftpConf", this.getSftpConf());
        parameter.put("remoteDir", this.getRemoteDir());

        reader.put("name", PluginName.HBase_R);
        reader.put("parameter", parameter);

        return reader;
    }

    @Override
    public String toReaderJsonString() {
        return toReaderJson().toJSONString();
    }

    @Override
    public void checkFormat(JSONObject data) {

    }
}
