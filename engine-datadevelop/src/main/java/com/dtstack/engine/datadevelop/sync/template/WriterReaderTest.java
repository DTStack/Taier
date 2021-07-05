package com.dtstack.batch.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.lineage.Column;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WriterReaderTest {

    @Test
    public void test(){
        java.util.List<java.util.Map<String, Object>> columns  = new ArrayList<>();
        java.util.Map<String, Object> m = new HashMap<>();
        m.put("name","shixi");
        m.put("type","int");
        m.put("key","sdsd");
        m.put("index","1");
        m.put("format","dsd");
        m.put("value","dsd");

        columns.add(m);

        java.util.List<java.util.Map<String, String>> columns2  = new ArrayList<>();
        java.util.Map<String, String> m22 = new HashMap<>();
        m22.put("name","shixi");
        m22.put("type","int");
        m22.put("key","sdsd");
        m22.put("index","1");
        m22.put("format","dsd");
        m22.put("value","dsd");

        columns2.add(m22);
        CarbonDataBase c = new CarbonDataBase();
        c.getDatabase();
        c.getDefaultFS();
        c.getHadoopConfig();
        c.getPath();
        c.getTable();

        CarbonDataReader c1 = new CarbonDataReader();
        c1.getColumn();
        c1.getFilter();
        c1.setColumn(columns);
        c1.toReaderJson();

        CarbonDataWriter c2 = new CarbonDataWriter();
        c2.setWriteMode("sdsdd");
        c2.getWriteMode();
        c2.getPartition();
        c2.setColumn(columns);
        c2.getColumn();
        c2.toWriterJson();
        JSONObject data = JSONObject.parseObject("{\n" +
                "        \"parameter\" : {\n" +

                "          \"customSql\" : \"\",\n" +
                "          \"index\" : \"2\",\n" +
                "          \"type\" : \"2\",\n" +
                "          \"bulkAction\" : \"232sd\",\n" +
                "          \"idColumn\" : [{\"2\":\"323\"}],\n" +
                "          \"username\" : \"232sd\",\n" +
                "          \"password\" : \"232sd\",\n" +
                "          \"address\" : \"172.1.1.1\",\n" +
                "          \"writeMode\" : \"22\",\n" +
                "          \"table\" : \"shixi_test\",\n" +
                "          \"database\" : \"shixi_test\",\n" +
                "          \"path\" : \"sssss\",\n" +
                "          \"startLocation\" : \"\",\n" +
                "          \"increColumn\" : \"\",\n" +
                "          \"column\" : [ {\n" +
                "            \"name\" : \"id\",\n" +
                "            \"type\" : \"INT\",\n" +
                "            \"key\" : \"id\"\n" +
                "          }, {\n" +
                "            \"name\" : \"name\",\n" +
                "            \"type\" : \"VARCHAR\",\n" +
                "            \"key\" : \"name\"\n" +
                "          }, {\n" +
                "            \"name\" : \"length\",\n" +
                "            \"type\" : \"BIGINT\",\n" +
                "            \"key\" : \"length\"\n" +
                "          }, {\n" +
                "            \"name\" : \"salary\",\n" +
                "            \"type\" : \"DOUBLE\",\n" +
                "            \"key\" : \"salary\"\n" +
                "          }, {\n" +
                "            \"name\" : \"birthday\",\n" +
                "            \"type\" : \"DATETIME\",\n" +
                "            \"key\" : \"birthday\"\n" +
                "          } ],\n" +
                "          \"connection\" : [ {\n" +
                "            \"sourceId\" : 11,\n" +
                "            \"password\" : \"******\",\n" +
                "            \"jdbcUrl\" : [ \"jdbc:mysql://172.16.101.136:3306/test\" ],\n" +
                "            \"type\" : 1,\n" +
                "            \"table\" : [ \"t_dtinsight_test\" ],\n" +
                "            \"username\" : \"drpeco\"\n" +
                "          } ],\n" +
                "          \"sourceIds\" : [ 11 ]\n" +
                "        },\n" +
                "        \"name\" : \"mysqlreader\"\n" +
                "      }");
        c2.checkFormat(data);

        EsReader e = new EsReader();
        e.setColumn(columns);
        e.toReaderJson();
        e.checkFormat(data);

        EsWriter e1 = new EsWriter();
        e1.setColumn(columns);
        e1.toWriterJson();
        e1.checkFormat(data);
        e1.setExtralConfig("{\"s\":1}");


        FtpReader f = new FtpReader();
        f.setFirstLineHeader(true);
        f.setColumn(columns);
        f.setPath("sdasdas/sdsd");
        f.toReaderJson();
        f.toReaderJsonString();

        FtpWriter f1 = new FtpWriter();
        f1.setColumn(columns);
        f1.toWriterJson();


        HBaseReader h =new HBaseReader();
        java.util.List<JSONObject> objectList = new ArrayList<>();
        JSONObject columnJson = new JSONObject();
        columnJson.put("key","rowkey");
        columnJson.put("cf","111");
        columnJson.put("type","int");
        columnJson.put("value","name");
        objectList.add(columnJson);
        h.setColumn(objectList);
        h.toReaderJson();

        HBaseWriter h1 = new HBaseWriter();
        h1.setColumn(objectList);
        h1.setRowkey("$(ss:id)");
        h1.setSrcColumns(Arrays.asList("id"));
        h1.toWriterJson();

        HDFSReader hd1 = new HDFSReader();
        hd1.setColumn(columns);
        hd1.toReaderJson();

        HDFSWriter hd2 = new HDFSWriter();
        hd2.setColumn(columns);
        hd2.toWriterJson();


        HiveReader hive1 = new HiveReader();
        hive1.setColumn(columns);
        hive1.toReaderJson();

        HiveWriter hiveWriter = new HiveWriter();
        hiveWriter.setColumn(columns);
        hiveWriter.toWriterJson();

        ImpalaHdfsReader i1 = new ImpalaHdfsReader();
        Column column = new Column();
        column.setName("sdsd");
        i1.setAllColumns(Arrays.asList(column));
        i1.setColumn(columns);
        i1.toReaderJson();
        ImpalaHdfsWriter i2 = new ImpalaHdfsWriter();
        i2.setColumn(columns);
        i2.toWriterJson();

        KuduReader kuduReader = new KuduReader();
        kuduReader.setColumn(columns2);
        kuduReader.toReaderJson();

        KuduWriter kuduWriter =new KuduWriter();
        kuduWriter.setColumn(columns2);
        kuduWriter.toWriterJson();

        MongoDbReader mongoDbReader =new MongoDbReader();
        mongoDbReader.setColumn(columns);
        mongoDbReader.toReaderJson();

        MongoDbWriter mongoDbWriter = new MongoDbWriter();
        mongoDbWriter.setColumn(columns);
        mongoDbWriter.toWriterJson();
    }
}
