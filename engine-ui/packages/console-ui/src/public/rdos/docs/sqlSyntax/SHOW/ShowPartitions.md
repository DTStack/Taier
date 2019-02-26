<<<<<<< HEAD
# Show Partitions
## Show Partitions

```sql
SHOW PARTITIONS table_name [PARTITION part_spec]

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)
```

显示表的分区，并可以通过 part_spec 属性过滤。此命令只适用于 Hive Format 建的表。
=======
# Show Partitions
## Show Partitions

```sql
SHOW PARTITIONS table_name [PARTITION part_spec]

part_spec:
	: (part_col_name1=val1, part_col_name2=val2, ...)
```

显示表的分区，并可以通过 part_spec 属性过滤。此命令只适用于 Hive Format 建的表。
>>>>>>> dev
