# Truncate Table
## Truncate Table

```sql

TRUNCATE TABLE table_name [PARTITION part_spec]

part_spec:
	: (part_col1=value1, part_col2=value2, ...)
```

删除表中的所有数据，或指定分区的数据。该表不能是临时表，外部表或视图

**PARTITION**

可通过 PARTITION 指定要删除的分区
