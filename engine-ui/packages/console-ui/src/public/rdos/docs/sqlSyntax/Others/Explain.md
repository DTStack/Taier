# Explain
## Explain

```sql
EXPLAIN [EXTENDED | CODEGEN] statement
```

输出 statement 的详细执行计划，不会实际运行。默认情况下，仅输出有关物理计划（physical plan）的信息。不支持解释 `DESCRIBE TABLE` 语句。

**EXTENDED**

Output information about the logical plan before and after analysis and optimization.

**CODEGEN**

Output the generated code for the statement, if any.
