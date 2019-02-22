# Set
## Set

```sql
SET [-v]
SET property_key[=property_value]

-- Example
set spark.sql.shuffle.partitions=1
```

设置属性，返回现有属性的值，或列出所有现有属性。如果为现有属性键提供了值，则将覆盖旧值

> Set 指令可用于 SparkSQL 的性能调优

**-v**

输出现有属性的含义

**[property_key]**

设置或返回单个属性的值
