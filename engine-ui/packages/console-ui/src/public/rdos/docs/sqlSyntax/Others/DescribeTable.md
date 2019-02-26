# Describe Table
## Describe Table

```sql
DESCRIBE [EXTENDED] table_name
```

返回表的元数据信息（字段名称、字段类型、comment 信息），若表不存在，会抛出异常

**EXTENDED**

展现表的明细信息，包括所属数据库、表类型、存储信息和属性等

## Describe Partition

```sql
DESCRIBE [EXTENDED] table_name PARTITION partition_spec
```

返回指定分区的元数据，partition_spec 中必须提供每个分区字段的值

**EXTENDED**

展现表的基本信息、指定分区的存储信息

## Describe Formatted

```sql
DESCRIBE FORMATTED table_name
```

返回表的明细信息
