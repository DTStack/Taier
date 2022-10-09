package com.dtstack.taier.datasource.plugin.hdfs.downloader.tableDownload;

import org.apache.parquet.io.InvalidRecordException;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:33 2020/9/1
 * @Description：Parquet 组信息
 */
public class GroupTypeIgnoreCase {
    private List<Type> fields;
    private Map<String, Integer> indexByName;

    public GroupTypeIgnoreCase(GroupType groupType) {
        this.fields = groupType.getFields();
        this.indexByName = new HashMap();

        for(int i = 0; i < this.fields.size(); ++i) {
            this.indexByName.put((this.fields.get(i)).getName().toLowerCase(), i);
        }

    }

    public boolean containsField(String name) {
        return this.indexByName.containsKey(name.toLowerCase());
    }

    public int getFieldIndex(String name) {
        name = name.toLowerCase();
        if (!this.indexByName.containsKey(name)) {
            throw new InvalidRecordException(name + " not found in " + this);
        } else {
            return (Integer)this.indexByName.get(name);
        }
    }
}
