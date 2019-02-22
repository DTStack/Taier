# Insert
## Insert From Query

```sql
	INSERT INTO [TABLE] [db_name.]table_name [PARTITION part_spec] select_statement
	
	INSERT OVERWRITE TABLE [db_name.]table_name [PARTITION part_spec] select_statement
	
	part_spec:
	    : (part_col_name1=val1 [, part_col_name2=val2, ...])
```

将查询结果插入一张表或分区

**OVERWRITE**

覆盖表或分区中的现有数据。若不指定本关键字，已有数据不会被清空，新的数据会附加上（Append）

**Example**

```sql
	-- Creates a partitioned native parquet table
	CREATE TABLE data_source_tab1 (col1 INT, p1 INT, p2 INT)
	  USING PARQUET PARTITIONED BY (p1, p2)
	
	-- Appends two rows into the partition (p1 = 3, p2 = 4)
	INSERT INTO data_source_tab1 PARTITION (p1 = 3, p2 = 4)
	  SELECT id FROM RANGE(1, 3)
	
	-- Overwrites the partition (p1 = 3, p2 = 4) using two new rows
	INSERT OVERWRITE TABLE default.data_source_tab1 PARTITION (p1 = 3, p2 = 4)
	  SELECT id FROM RANGE(3, 5)
```

## Insert Values

```sql
	INSERT INTO [TABLE] [db_name.]table_name [PARTITION part_spec] VALUES values_row [, values_row ...]
	
	INSERT OVERWRITE TABLE [db_name.]table_name [PARTITION part_spec] VALUES values_row [, values_row ...]
	
	values_row:
	    : (val1 [, val2, ...])
```

直接将一组值插入到表或分区中

**OVERWRITE**

覆盖表或分区中的现有数据。若不指定本关键字，已有数据不会被清空，新的数据会附加上（Append）

**Example**

```sql
	-- Creates a partitioned hive serde table (using the HiveQL syntax)
	CREATE TABLE hive_serde_tab1 (col1 INT, p1 INT, p2 INT)
	  USING HIVE OPTIONS(fileFormat 'PARQUET') PARTITIONED BY (p1, p2)
	
	-- Appends two rows into the partition (p1 = 3, p2 = 4)
	INSERT INTO hive_serde_tab1 PARTITION (p1 = 3, p2 = 4)
	  VALUES (1), (2)
	
	-- Overwrites the partition (p1 = 3, p2 = 4) using two new rows
	INSERT OVERWRITE hive_serde_tab1 PARTITION (p1 = 3, p2 = 4)
	  VALUES (3), (4)
```

## Dynamic Partition Inserts

如果 part_spec 未完全指定，这种插入方式成为**动态分区插入**，也称为**多分区插入**。在 part_spec 中，分区列值是可选的。未给出值的列，成为动态分区列，给出值的列，被称为静态分区列。例如，part_spec 写为 `(p1=3, p2, p3)` 具有静态分区列 `(p1)` 和两个动态分区列 `(p2和p3)`

在 part_spec 中，静态分区列（指定常量值的列）必须写在动态分区列（未指定常量值的列）之前


> 动态分区列的值在运行时被指定。动态分区字段必须在 part_spec 和输入结果集之间被指定，输入结果集，可以是每行指定的值，或者 SELECT 查询结果。动态分区的字段值是根据位置来解析的，而不是通过名字，而且顺序必须准确匹配。


> DTinsightBatch 默认打开动态分区的配置



> 在动态分区模式下，输入结果集可能产生大量动态分区，这会产生大量的分区目录
> 
> **OVERWRITE**
> 
> 根据目标表的类型不同，语义不同
> 
> 1. Hive SerDe 表: `INSERT OVERWRITE` 不会删除已有的分区，只会覆盖那些在运行时写入数据的分区。这与 Apache Hive 语义相匹配。对于 Hive SerDe 表，Spark SQL 会遵循 Hive 相关的配置，包括 `hive.exec.dynamic.partition` 和 `hive.exec.dynamic.partition.mode`
> 2. Native data source 表: `INSERT OVERWRITE` 执行时，会先删除所有匹配的分区（例如：PARTITION(a=1, b)），然后插入所有剩余的值

> DTinsightBatch 目前仅支持 Hive SerDe 类型的表，所以 Native data source 只存在于原生 SparkSQL，在 DTinsightBatch 上操作时，将一直采用 Hive SerDe 模式的表

**Examples**

```sql
-- Create a partitioned native Parquet table
CREATE TABLE data_source_tab2 (col1 INT, p1 STRING, p2 STRING)
	USING PARQUET PARTITIONED BY (p1, p2)

-- Two partitions ('part1', 'part1') and ('part1', 'part2') are created by this dynamic insert.
-- The dynamic partition column p2 is resolved by the last column `'part' || id`
INSERT INTO data_source_tab2 PARTITION (p1 = 'part1', p2)
	SELECT id, 'part' || id FROM RANGE(1, 3)

-- A new partition ('partNew1', 'partNew2') is added by this INSERT OVERWRITE.
INSERT OVERWRITE TABLE data_source_tab2 PARTITION (p1 = 'partNew1', p2)
	VALUES (3, 'partNew2')

-- After this INSERT OVERWRITE, the two partitions ('part1', 'part1') and ('part1', 'part2') are dropped,
-- because both partitions are included by (p1 = 'part1', p2).
-- Then, two partitions ('partNew1', 'partNew2'), ('part1', 'part1') exist after this operation.
INSERT OVERWRITE TABLE data_source_tab2 PARTITION (p1 = 'part1', p2)
	VALUES (5, 'part1')


-- Create and fill a partitioned hive serde table with three partitions:
-- ('part1', 'part1'), ('part1', 'part2') and ('partNew1', 'partNew2')
CREATE TABLE hive_serde_tab2 (col1 INT, p1 STRING, p2 STRING)
	USING HIVE OPTIONS(fileFormat 'PARQUET') PARTITIONED BY (p1, p2)
INSERT INTO hive_serde_tab2 PARTITION (p1 = 'part1', p2)
	SELECT id, 'part' || id FROM RANGE(1, 3)
INSERT OVERWRITE TABLE hive_serde_tab2 PARTITION (p1 = 'partNew1', p2)
	VALUES (3, 'partNew2')

-- After this INSERT OVERWRITE, only the partitions ('part1', 'part1') is overwritten by the new value.
-- All the three partitions still exist.
INSERT OVERWRITE TABLE hive_serde_tab2 PARTITION (p1 = 'part1', p2)
	VALUES (5, 'part1')
```
