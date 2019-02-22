# Drop Table
## Drop Table

```sql
DROP TABLE [IF EXISTS] table_name
```

若未内部表，则删除表，并从文件系统中删除对应的数据，若表不存在，则抛出异常。

**IF EXISTS**

若表不存在，则无任何变化
