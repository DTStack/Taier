package com.dtstack.engine.sql.hive;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.handler.IUglySqlHandler;
import com.dtstack.engine.sql.handler.ImpalaUglySqlHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSqlParser {
    static IUglySqlHandler iUglySqlHandler = new ImpalaUglySqlHandler();
    static AstNodeParser astNodeParser = new AstNodeParser(iUglySqlHandler);
    static Map<String, List<Column>> tableColumnMap = new HashMap<>();

    static {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id", 0));
        columns.add(new Column("name", 1));
        columns.add(new Column("text", 2));
        columns.add(new Column("address", 3));

        List<Column> columns1 = new ArrayList<>();
        columns1.add(new Column("id1", 0));
        columns1.add(new Column("name1", 1));
        columns1.add(new Column("text1", 2));
        columns1.add(new Column("address1", 3));
        tableColumnMap.put("shixi.b", columns1);

        List<Column> columns2 = new ArrayList<>();
        columns2.add(new Column("id2", 0));
        columns2.add(new Column("name2", 1));
        columns2.add(new Column("text2", 2));
        columns2.add(new Column("address2", 3));

        List<Column> columns3 = new ArrayList<>();
        columns3.add(new Column("id3", 0));
        columns3.add(new Column("name3", 1));
        columns3.add(new Column("text3", 2));
        columns3.add(new Column("address3", 3));
        tableColumnMap.put("shixi.a", columns);
        tableColumnMap.put("shixi.c", columns2);
        tableColumnMap.put("shixi.d", columns3);
    }

    @Test
    public void simpleSql() throws Exception {
        String sql = "create table a as select id1,name1 from b";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertEquals(p.getMainTable().getName(), "a");
        Assert.assertEquals(p.getColumnLineages().size(), 2);
    }

    @Test
    public void joinSql() throws Exception {
        String sql = "create table a as select id1,name1,id2,name2 from b b left join c c on c.id2 = b.id1";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 4);
    }

    @Test
    public void joinOtherSql() throws Exception {
        String sql = "create table a as select id1,name1,id2,name2 from b,c";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 4);
    }

    @Test
    public void sonSelectSql() throws Exception {
        String sql = "create table a as select id2,name2 from (select id2, name2 from c) b";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 2);
    }

    @Test
    public void joinSonSelectSql() throws Exception {
        String sql = "create table a as select id2,name2,id3,name3 from (select id2, name2 from c)b left join d d on b.id = d.id";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 4);
    }


    @Test
    public void unionSelectSql() throws Exception {
        String sql = "create table a as select id1,name1,id2,name2 from (select id2, name2 from c union select id1,name1 from d)b ";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 4);
    }

    @Test
    public void functionSelectSql() throws Exception {
        String sql = "create table a as select nvl(id1),name1 from b ";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 2);
    }

    @Test
    public void moreFunctionSelectSql() throws Exception {
        String sql = "create table a as select nvl(nvl(id1),address1)as id,name1 from b ";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 3);

        sql = "create table a as select nvl(nvl(id1),address1) as id,(id1+1000) as `id1`,`name1` from b ";
        p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 3);
    }

    @Test
    public void caseWhenSelectSql() throws Exception {
        String sql = "create table a as select case when id1>1 then name1 else address1 end as id from b ";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getColumnLineages().size(), 3);

    }

    @Test
    public void createViewSelectSql() throws Exception {
        String sql = "create view nanqi_view_042711 as select * from nanqi ";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertNotNull(p.getColumnLineages());
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE_AS);

    }

    @Test
    public void createExitsSql() throws Exception {
        String sql = "create table if not exists tb_regress_hiveSQL_1(id int,name string) partitioned by (pt STRING) stored as textfile";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE);
        Assert.assertEquals(p.getMainTable().isIgnore(), true);

    }

    @Test
    public void createSql() throws Exception {
        String sql = "create table if not exists tb_regression_datatype(\n" +
                "    id int,\n" +
                "    tid TINYINT,\n" +
                "    sid SMALLINT,\n" +
                "    bid bigint,\n" +
                "    is_new boolean,\n" +
                "    price float,\n" +
                "    amount double,\n" +
                "    allcount DECIMAL,\n" +
                "    comments string,\n" +
                "    caseno char(10),\n" +
                "    remark VARCHAR,\n" +
                "    create_time TIMESTAMP,\n" +
                "    struct_primitive_obj STRUCT < name : string COMMENT 'comment_string'>,\n" +
                "    array_primitive_obj ARRAY <int>,\n" +
                "    array_struct_obj ARRAY < STRUCT <\n" +
                "        category: STRING\n" +
                "      , international_code: STRING\n" +
                "      , area_code: STRING\n" +
                "      , exchange: STRING\n" +
                "      , extension: STRING\n" +
                "      , mobile: BOOLEAN\n" +
                "      , carrier: STRING\n" +
                "      , is_current: BOOLEAN\n" +
                "      , service_start_date: TIMESTAMP\n" +
                "      , service_end_date: TIMESTAMP\n" +
                "    >>,\n" +
                "    struct_array_obj STRUCT < userinfo: ARRAY<int> COMMENT 'comment_arrayint'>,\n" +
                "    map_primitive_obj MAP <int,string>,\n" +
                "    map_struct_obj MAP<int,STRUCT<age:INT > >,\n" +
                "    map_array_obj MAP<int,ARRAY <int> >,\n" +
                "    map_complex_obj MAP<String,STRUCT < account: ARRAY<int> COMMENT 'comment_arrayint'>>\n" +
                ")PARTITIONED BY (pt string,dt string) STORED AS TEXTFILE";
        ParseResult p = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(p.getSqlType(), SqlType.CREATE);
        Assert.assertEquals(p.getMainTable().isIgnore(), true);
    }

    @Test
    public void lateralViewCreate() throws Exception {
        String sql = "create table s1 as select id , t_hobby.sss,t_hobby.bbb,t_hobby1.qqq,t_hobby1.www from a lateral view explode(split(name, ',')) t_hobby as sss,bbb lateral view explode(split(text, ',')) t_hobby1 as qqq,www";

        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE_AS);
    }

    @Test
    public void ssssssCreate() throws Exception {
        String sql = "CREATE TABLE if not exists sorted_census_data\n" +
                "  SORT BY(\n" +
                "    last_name ,\n" +
                "    sstate\n" +
                " )STORED AS PARQUET\n" +
                "AS SELECT\n" +
                "    last_name,\n" +
                "    first_name,\n" +
                "    state,\n" +
                "    address     \n" +
                "FROM\n" +
                "    unsorted_census_data";
        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE_AS);
        Assert.assertEquals(result.getMainTable().getName(), "sorted_census_data");

    }

    @Test
    public void sortByCreate() throws Exception {
        String sql = "CREATE TABLE if not exists census_data(\n" +
                "    last_name STRING,\n" +
                "    first_name STRING,\n" +
                "    state STRING,\n" +
                "    address STRING\n" +
                " )SORT BY(\n" +
                "    last_name,\n" +
                "    state\n" +
                " )STORED AS PARQUET;";

        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE);
        Assert.assertEquals(result.getMainTable().getName(), "census_data");

    }

    @Test
    public void likeCreate() throws Exception {
        String sql = "create table tb_craft_0507_2 LIKE tb_craft_0507_1";
        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE_LIKE);
        Assert.assertEquals(result.getMainTable().getName(), "tb_craft_0507_2");
    }

    @Test
    public void fzCreate() throws Exception {
        String sql = "Create table test_net_ods_mybank_accounting(\n" +
                "bsn_type STRING,\n" +
                "accounting_type STRING,\n" +
                "accounting_amt BIGINT,\n" +
                "cdate STRING\n" +
                ")\n" +
                "ROW FORMAT DELIMITED FIELDS TERMINATED BY ','\n" +
                "LINES TERMINATED BY '\\n' WITH SERDEPROPERTIES('field.delim'=',',\n" +
                "'line.delim' = '\\n','serialization.format'=',')\n" +
                "stored as textfile";
        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE);
        Assert.assertEquals(result.getMainTable().getName(), "test_net_ods_mybank_accounting");
    }

    @Test
    public void innerCreate() throws Exception {
        String sql = "create table tb_craft_0507_2 as select id from a inner join b on a.id = b.id";
        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE_AS);
        Assert.assertEquals(result.getMainTable().getName(), "tb_craft_0507_2");
    }

    @Test
    public void createAsCreate() throws Exception {
        String sql = "create table if not exists tb_craft_0507_1\n" +
                "as select\n" +
                "    * \n" +
                "from\n" +
                "    (select\n" +
                "        L.id as L_id,\n" +
                "        L.name as L_name,\n" +
                "        L.idcard as L_idcard,\n" +
                "        L.birthday as L_birthday,\n" +
                "        L.mobile as L_mobile,\n" +
                "        L.email as L_email,\n" +
                "        L.gender as L_gender,\n" +
                "        R.id as R_id,\n" +
                "        R.name as R_name,\n" +
                "        R.idcard as R_idcard,\n" +
                "        R.birthday as R_birthday,\n" +
                "        R.mobile as R_mobile,\n" +
                "        R.email as R_email,\n" +
                "        R.gender as R_gender \n" +
                "    from\n" +
                "        shier_user_1 L \n" +
                "    left join\n" +
                "        shier_user R \n" +
                "            on 1=1 \n" +
                "            AND L.id = R.id \n" +
                "    where\n" +
                "        (\n" +
                "            R.id is null \n" +
                "            or L.name!=R.name \n" +
                "            or L.idcard!=R.idcard \n" +
                "            or L.birthday!=R.birthday \n" +
                "            or L.mobile!=R.mobile \n" +
                "            or L.email!=R.email \n" +
                "            or L.gender!=R.gender \n" +
                "            or (\n" +
                "                L.gender is null \n" +
                "                and R.gender is not null \n" +
                "                or L.gender is not null \n" +
                "                and R.gender is null\n" +
                "            )\n" +
                "        ) \n" +
                "        and L.pt='20200428' \n" +
                "    union\n" +
                "    select\n" +
                "        L.id as L_id,\n" +
                "        L.name as L_name,\n" +
                "        L.idcard as L_idcard,\n" +
                "        L.birthday as L_birthday,\n" +
                "        L.mobile as L_mobile,\n" +
                "        L.email as L_email,\n" +
                "        L.gender as L_gender,\n" +
                "        R.id as R_id,\n" +
                "        R.name as R_name,\n" +
                "        R.idcard as R_idcard,\n" +
                "        R.birthday as R_birthday,\n" +
                "        R.mobile as R_mobile,\n" +
                "        R.email as R_email,\n" +
                "        R.gender as R_gender \n" +
                "    from\n" +
                "        shier_user_1 L \n" +
                "    right join\n" +
                "        shier_user R \n" +
                "            on 1=1 \n" +
                "            AND L.id = R.id \n" +
                "    where\n" +
                "        (\n" +
                "            L.id is null \n" +
                "            or L.name!=R.name \n" +
                "            or L.idcard!=R.idcard \n" +
                "            or L.birthday!=R.birthday \n" +
                "            or L.mobile!=R.mobile \n" +
                "            or L.email!=R.email \n" +
                "            or L.gender!=R.gender \n" +
                "            or (\n" +
                "                L.gender is null \n" +
                "                and R.gender is not null \n" +
                "                or L.gender is not null \n" +
                "                and R.gender is null\n" +
                "            )\n" +
                "        ) \n" +
                "        and L.pt='20200428'\n" +
                ") t";

        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE_AS);
        Assert.assertEquals(result.getMainTable().getName(), "tb_craft_0507_1");

    }

    @Test
    public void yhSql() throws Exception {
        String sql = "create table `hhh.sss` as SELECT\n" +
                "    c1 AS `EmployeeID`,\n" +
                "    c2 AS `Dateofhire`  \n" +
                "FROM\n" +
                "    `t1`  where id = 1";

        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        Assert.assertEquals(parseResult.getSqlType(), SqlType.CREATE_AS);
        System.out.println(parseResult.toString());

        List<Table> tables = astNodeParser.parseTables("shixi", sql);
        System.out.println(tables.size());
    }

    @Test
    public void createzorefivetwotwoSql() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS `userinfo_ash`(\n" +
                "    `id` INT COMMENT'',\n" +
                "    `name` STRING COMMENT'',\n" +
                "    `age` INT COMMENT''\n" +
                " )COMMENT'' partitioned by(\n" +
                "    pt STRING\n" +
                " )stored as parquet";

        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        System.out.println(parseResult.toString());

    }

    @Test
    public void ddddsadasdsdsdSql() throws Exception {
        String sql = "select distinct a.* from (select * from a where id in (select max(id) as id_max from b group by cust_no)) a" +
                " inner join a  b on a.cust_no = b.cust_no inner Join a c on b.id_no = c.cert_no inner join  a d on c.contract_no = d.contract_no" +
                " where to_date(c.encash_date) = \"2019-12-01\" and to_date(c.encash_date)<\"2020-02-01\" and " +
                "c.cert_no not in (select cert_no from ss  where conreact_no in ( select contract_no from dsd where status = \"OVD\")) " +
                "order by rand(12345) limit 40000";

        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        System.out.println(parseResult.toString());

    }


    @Test
    public void createLifecycleSql() throws Exception {
        String sql = "CREATE TABLE `net_ods_abs_cust_info_base_df`(\n" +
                "    `id` STRING COMMENT'ID',\n" +
                "    `proj_no` STRING COMMENT'项目编号',\n" +
                "    `cust_name` STRING COMMENT'客户名称',\n" +
                "    `cust_type` STRING COMMENT'客户类型：\n" +
                "01-农户\n" +
                "02-工薪\n" +
                "03-个体工商户 \n" +
                "04-学生\n" +
                "06-企业法定代表人\n" +
                "99-其他',\n" +
                "    `id_type` STRING COMMENT'证件类型：\n" +
                "0-身份证\n" +
                "1-户口簿 \n" +
                "2-护照\n" +
                "3-军官证\n" +
                "4-士兵证\n" +
                "5-港澳居民来往内地通行证\n" +
                "6-台湾同胞来往内地通行证\n" +
                "7-临时身份证\n" +
                "8-外国人居留证\n" +
                "9-警官证\n" +
                "A-香港身份证\n" +
                "B-澳门身',\n" +
                "    `id_no` STRING COMMENT'证件号码',\n" +
                "    `id_source` STRING COMMENT'发证机关',\n" +
                "    `id_start_date` STRING COMMENT'证件签发日期 yyyyMMdd',\n" +
                "    `id_end_date` STRING COMMENT'证件失效日期 yyyyMMdd',\n" +
                "    `d_license` STRING COMMENT'驾驶证',\n" +
                "    `sex` STRING COMMENT'性别：\n" +
                "0-未知的性别  \n" +
                "1-男性  \n" +
                "2-女性  \n" +
                "9-未说明性别',\n" +
                "    `birth` STRING COMMENT'出生日期：yyyyMMdd',\n" +
                "    `nation` STRING COMMENT'民族',\n" +
                "    `work_type` STRING COMMENT'职业：\n" +
                "0-国家机关、党群组织、企业、事业单位负责人\n" +
                "1-专业技术人员\n" +
                "3-办事人员和有关人员\n" +
                "4-商业、服务业人员\n" +
                "5-农、林、牧、渔、水利业生产人员\n" +
                "6-生产、运输设备操作人员及有关人员\n" +
                "X-军人\n" +
                "Y-不便分类的其他从业人员\n" +
                "Z-未知',\n" +
                "    `edu` STRING COMMENT'最高学历：\n" +
                "            10-研究生\n" +
                "            20-大学本科（简称“大学”）\n" +
                "            30-大学专科和专科学校（简称“大专”）\n" +
                "            40-中等专业学校或中等技术学校\n" +
                "            50-技术学校\n" +
                "            60-高中\n" +
                "            70-初中\n" +
                "            80-小学\n" +
                "            90-文盲或半文盲\n" +
                "            99-未知',\n" +
                "    `degree` STRING COMMENT'最高学位：\n" +
                "            0-其他\n" +
                "            1-名誉博士\n" +
                "            2-博士\n" +
                "            3-硕士\n" +
                "            4-学士\n" +
                "            9-未知',\n" +
                "    `graduation_time` STRING COMMENT'毕业时间 yyyyMMdd',\n" +
                "    `certificate_type` STRING COMMENT'学历证书类型',\n" +
                "    `certificate_no` STRING COMMENT'学历证书编号',\n" +
                "    `certificate_get_time` STRING COMMENT'学历证书取得时间 yyyyMMdd',\n" +
                "    `marriage` STRING COMMENT'婚姻状况：\n" +
                "  1 未婚\n" +
                "  2 已婚\n" +
                "  3 丧偶\n" +
                "  4 离婚\n" +
                "  5 其他\n" +
                "  9 未知',\n" +
                "    `marriage_register_date` STRING COMMENT'婚姻登记日期 yyyyMMdd',\n" +
                "    `mate_name` STRING COMMENT'配偶名称',\n" +
                "    `mate_idtype` STRING COMMENT'证件类型：\n" +
                "0-身份证\n" +
                "1-户口簿 \n" +
                "2-护照\n" +
                "3-军官证\n" +
                "4-士兵证\n" +
                "5-港澳居民来往内地通行证\n" +
                "6-台湾同胞来往内地通行证\n" +
                "7-临时身份证\n" +
                "8-外国人居留证\n" +
                "9-警官证\n" +
                "A-香港身份证\n" +
                "B-澳门身',\n" +
                "    `mate_idno` STRING COMMENT'配偶证件号码',\n" +
                "    `mate_work` STRING COMMENT'配偶工作单位',\n" +
                "    `mate_tel` STRING COMMENT'配偶联系电话',\n" +
                "    `doc_no_where_province` STRING COMMENT'身份证地址(省)',\n" +
                "    `doc_no_where_city` STRING COMMENT'身份证地址(市)',\n" +
                "    `doc_no_where_district` STRING COMMENT'身份证地址(区)',\n" +
                "    `id_valid_start_date` STRING COMMENT'身份证有效期(始于)yyyyMMdd',\n" +
                "    `id_valid_end_date` STRING COMMENT'身份证有效期(止于)yyyyMMdd',\n" +
                "    `doc_address` STRING COMMENT'身份证详细地址',\n" +
                "    `native_type` STRING COMMENT'户籍类型',\n" +
                "    `home_area` STRING COMMENT'户籍所在地：\n" +
                "            《行政区划》\n" +
                "            无法填报默认000000（中国）',\n" +
                "    `reltto_house_owner` STRING COMMENT'与户主关系',\n" +
                "    `issuer_provine` STRING COMMENT'户籍所在省',\n" +
                "    `issuer_city` STRING COMMENT'户籍所在市',\n" +
                "    `issuer_district` STRING COMMENT'户籍所在区',\n" +
                "    `issuer_addr` STRING COMMENT'户籍详细地址',\n" +
                "    `issuer_code` STRING COMMENT'户籍邮编',\n" +
                "    `house_owner_page_city` STRING COMMENT'户主页地址(市)',\n" +
                "    `house_owner_page_district` STRING COMMENT'户主页地址(区)',\n" +
                "    `house_owner_page_province` STRING COMMENT'户主页地址(省)',\n" +
                "    `post_addr` STRING COMMENT'通讯地址',\n" +
                "    `post_code` STRING COMMENT'邮政编码',\n" +
                "    `is_id_sameto_live_city` STRING COMMENT'身份证所在城市是否同现居住城市',\n" +
                "    `home_district` STRING COMMENT'家庭所在区',\n" +
                "    `home_city` STRING COMMENT'家庭所在城市',\n" +
                "    `home_provine` STRING COMMENT'家庭所在省份',\n" +
                "    `home_addr` STRING COMMENT'家庭住宅住址',\n" +
                "    `home_code` STRING COMMENT'家庭住宅邮编',\n" +
                "    `home_tel` STRING COMMENT'家庭住宅电话',\n" +
                "    `address_priority` STRING COMMENT'地址优先级',\n" +
                "    `phone_no` STRING COMMENT'手机号码',\n" +
                "    `mobile_verify_status` STRING COMMENT'手机认证状态',\n" +
                "    `tel_no` STRING COMMENT'联系电话',\n" +
                "    `home_sts` STRING COMMENT'居住状况：\n" +
                "            1-自置\n" +
                "            2-按揭\n" +
                "            3-亲属楼宇\n" +
                "            4-集体宿舍\n" +
                "            5-租房\n" +
                "            6-共有住宅\n" +
                "            7-其他\n" +
                "            9-未知。\n" +
                "            无法填报默认9',\n" +
                "    `live_with` STRING COMMENT'和谁生活',\n" +
                "    `live_city` STRING COMMENT'居住所在城市',\n" +
                "    `live_province` STRING COMMENT'居住所在省份',\n" +
                "    `local_live_year` BIGINT COMMENT'本地居住年限',\n" +
                "    `now_live_year` BIGINT COMMENT'现住址居住年限',\n" +
                "    `qq` STRING COMMENT'QQ号',\n" +
                "    `weixin` STRING COMMENT'微信号',\n" +
                "    `email` STRING COMMENT'电子邮箱',\n" +
                "    `relatives_name` STRING COMMENT'直系亲属姓名',\n" +
                "    `relatives_mobile` STRING COMMENT'直系亲属手机号',\n" +
                "    `children` STRING COMMENT'是否有子女：\n" +
                "            0-否\n" +
                "            1-是\n" +
                "            2-未知\n" +
                "            无法填报默认2',\n" +
                "    `children_amount` BIGINT COMMENT'子女数量',\n" +
                "    `provider_num` BIGINT COMMENT'供养人数',\n" +
                "    `ann_income` STRING COMMENT'年收入',\n" +
                "    `esign_account_id` STRING COMMENT'e签宝的用户id',\n" +
                "    `sys_company_code` STRING COMMENT'所属公司代码',\n" +
                "    `sys_org_code` STRING COMMENT'所属部门代码',\n" +
                "    `create_by` STRING COMMENT'创建人',\n" +
                "    `create_date` STRING COMMENT'创建时间',\n" +
                "    `update_by` STRING COMMENT'修改人',\n" +
                "    `update_date` STRING COMMENT'修改时间',\n" +
                "    `isLegalRep` STRING COMMENT'是否为法人代表Y：为法定代表人；\n" +
                "N：为占股最多的自然人股东。',\n" +
                "    `bankAccount` STRING COMMENT'借款人的一类银行卡卡号',\n" +
                "    `bankName` STRING COMMENT'借款人的一类银行卡发卡行名称',\n" +
                "    `authorization` STRING COMMENT'客户允许华夏查询客户央行征信报告的授权文件签署(是，授权查询；否，未授权查询)',\n" +
                "    `bankCardNo` STRING COMMENT'二类户账号'\n" +
                " )comment'客户基本信息表'stored as parquet";

        ParseResult parseResult = astNodeParser.parseSql(sql, "shixi", new HashMap<>());
        System.out.println(parseResult.toString());
    }

    @Test
    public void withSelectQtSql() throws Exception {
        String sql = "CREATE TABLE TABLE_WITH AS\n" +
                "with t_with_1 as (select\n" +
                "    * \n" +
                "from\n" +
                "    a \n" +
                "where\n" +
                "    age>25), t_with_2 as (select\n" +
                "    id1 as id ,name1 as name,text1 as text,address1 as address  \n" +
                "from\n" +
                "    b \n" +
                "where\n" +
                "    age>25) \n" +
                "    select\n" +
                "        * \n" +
                "    from\n" +
                "        t_with_1 \n" +
                "    union\n" +
                "    select\n" +
                "        * \n" +
                "    from\n" +
                "        t_with_2\n" +
                ";";
        sql = sql.replaceAll("\n"," ");
        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE_AS);
        Assert.assertEquals(result.getColumnLineages().size(), 8);
    }


    @Test
    public void withCreateSelectSql() throws Exception {
        String sql = "CREATE TABLE TABLE_WITH AS\n" +
                "with \n" +
                "    t_with_1 as (select * from customers where age>25), \n" +
                "    t_with_2 as (select * from employee where age>25) " +
                "    select * from t_with_1 union select * from t_with_2\n" +
                "";
        sql = sql.replaceAll("\n"," ");
        ParseResult result = astNodeParser.parseSql(sql, "shixi", tableColumnMap);
        Assert.assertEquals(result.getSqlType(), SqlType.CREATE_AS);
        Assert.assertEquals(result.getColumnLineages().size(), 0);
    }
}
