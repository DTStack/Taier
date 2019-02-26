# CreateTable
## Create Table with Hive format

```sql
CREATE [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
[(col_name1[:] col_type1 [COMMENT col_comment1], ...)]
[COMMENT table_comment]
[PARTITIONED BY (col_name2[:] col_type2 [COMMENT col_comment2], ...)]
[ROW FORMAT row_format]
[STORED AS file_format]
[LOCATION path]
[TBLPROPERTIES (key1=val1, key2=val2, ...)]
[AS select_statement]
[LIFECYCLE days]

row_format:
: SERDE serde_cls [WITH SERDEPROPERTIES (key1=val1, key2=val2, ...)]
| DELIMITED [FIELDS TERMINATED BY char [ESCAPED BY char]]
[COLLECTION ITEMS TERMINATED BY char]
[MAP KEYS TERMINATED BY char]
[LINES TERMINATED BY char]
[NULL DEFINED AS char]

file_format:
: TEXTFILE | SEQUENCEFILE | RCFILE | ORC | PARQUET | AVRO
| INPUTFORMAT input_fmt OUTPUTFORMAT output_fmt
```

使用 Hive Format 建表，若表已在数据库中存在，则会抛出异常。执行删表之后，数据将会从文件系统中被删除

**EXTERNAL**

建外部表，使用 LOCATION 指定的自定义目录。查询时，访问指定目录中的数据。删除外部表时，数据不会从文件系统中删除。若指定了 LOCATION，相当于隐含指定 EXTERNAL 属性。

**IF NOT EXISTS**

若表已在数据库中存在，则无任何变化

**PARTITIONED BY**

按照指定字段对表进行分区，分区字段必须与非分区字段分别开来。

分区字段不能在 AS <select_statement>查询语句中被指定

**ROW FORMAT**

可使用 SERDE 子句为此表指定自定义 SerDe。

否则，可使用 DELIMITED 子句来使用原生的 SerDe 并指定分隔符，转义字符，空字符等。

**STORED AS**

指定此表的存储文件格式。可用的格式包括 TEXTFILE，SEQUENCEFILE，RCFILE，ORC，PARQUET 和 AVRO。可以通过 INPUTFORMAT 和 OUTPUTFORMAT 参数指定输入和输出格式。


> ROW FORMAT SERDE 必须与 TEXTFILE、SEQUENCEFILE 或 RCFILE 格式一起使用

> ROW FORMAT DELIMITED 必须与 TEXTFILE 格式一起使用

> DTinsightBatch 默认使用 ORC 格式建表，同时可以对 TEXTFILE、PARQUET 格式进行数据同步或页面 SELECT 数据下载。SEQUENCEFILE、RCFILE、AVRO 格式，进行 SQL 建表时，可以建表成功，但不能对这些表进行数据同步或页面 SELECT 数据下载。

**LOCATION**

指定表的存储路径。指定 LOCATION，会自动建成外部表

**AS [select_statement]**

使用 select 的结果填充表

**LIFECYCLE**

指定表的生命周期属性，此属性仅 DTinsightBatch 独有，原生 SparkSQL 不支持生命周期，LIFECYCLE 后面跟随的是生命周期，单位为天，至少为 1 天。若用户未指定 LIFECYCLE，则默认生命周期为 9999 天

**Example**

```sql
CREATE TABLE my_table (name STRING, age INT)

CREATE TABLE my_table (name STRING, age INT)
COMMENT 'This table is partitioned'
PARTITIONED BY (hair_color STRING COMMENT 'This is a column comment')
TBLPROPERTIES ('status'='staging', 'owner'='andrew')

CREATE TABLE my_table (name STRING, age INT)
COMMENT 'This table specifies a custom SerDe'
ROW FORMAT SERDE 'org.apache.hadoop.hive.ql.io.orc.OrcSerde'
STORED AS
INPUTFORMAT 'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat'
OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'

CREATE TABLE my_table (name STRING, age INT)
COMMENT 'This table uses the CSV format'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE

CREATE TABLE your_table
COMMENT 'This table is created with existing data'
AS SELECT * FROM my_table

CREATE EXTERNAL TABLE IF NOT EXISTS my_table (name STRING, age INT)
COMMENT 'This table is created with existing data'
LOCATION 'spark-warehouse/tables/my_existing_table'
```

## Create Table Like

```sql
CREATE TABLE [IF NOT EXISTS] table_name1 LIKE table_name2 [LOCATION path]
```

使用已有的表或视图的定义/元数据创建表。
