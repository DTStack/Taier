<<<<<<< HEAD
# Alter Table Partitions
## 语法

```sql
ALTER TABLE table_name ADD [IF NOT EXISTS]
PARTITION part_spec [LOCATION path], ...

part_spec:
: (part_col_name1=val1, part_col_name2=val2, ...)

-- Example：
ALTER TABLE aa ADD IF NOT EXISTS PARTITION (pt='0102') ;
```

**IF NOT EXISTS**

若指定的分区已存在，则无任何变化

```sql

ALTER TABLE table_name PARTITION part_spec RENAME TO PARTITION part_spec

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)

-- Example：
ALTER TABLE aa PARTITION (pt='0102') RENAME TO PARTITION (pt='muyun0102');


ALTER TABLE table_name DROP [IF EXISTS] (PARTITION part_spec, ...)

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)
```
**IF EXISTS**

若指定的分区不存在，则无任何变化

```sql

ALTER TABLE table_name PARTITION part_spec SET LOCATION path

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)
```

=======
# Alter Table Partitions
## 语法

```sql
ALTER TABLE table_name ADD [IF NOT EXISTS]
PARTITION part_spec [LOCATION path], ...

part_spec:
: (part_col_name1=val1, part_col_name2=val2, ...)

-- Example：
ALTER TABLE aa ADD IF NOT EXISTS PARTITION (pt='0102') ;
```

**IF NOT EXISTS**

若指定的分区已存在，则无任何变化

```sql

ALTER TABLE table_name PARTITION part_spec RENAME TO PARTITION part_spec

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)

-- Example：
ALTER TABLE aa PARTITION (pt='0102') RENAME TO PARTITION (pt='muyun0102');


ALTER TABLE table_name DROP [IF EXISTS] (PARTITION part_spec, ...)

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)
```
**IF EXISTS**

若指定的分区不存在，则无任何变化

```sql

ALTER TABLE table_name PARTITION part_spec SET LOCATION path

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)
```

>>>>>>> dev
