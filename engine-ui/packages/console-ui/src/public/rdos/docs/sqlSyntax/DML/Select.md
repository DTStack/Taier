<<<<<<< HEAD
# Select
## Select

```sql
SELECT [hints, ...] [ALL|DISTINCT] named_expression[, named_expression, ...]
	FROM relation[, relation, ...]
	[lateral_view[, lateral_view, ...]]
	[WHERE boolean_expression]
	[aggregation [HAVING boolean_expression]]
	[ORDER BY sort_expressions]
	[CLUSTER BY expressions]
	[DISTRIBUTE BY expressions]
	[SORT BY sort_expressions]
	[WINDOW named_window[, WINDOW named_window, ...]]
	[LIMIT num_rows]

named_expression:
	: expression [AS alias]

relation:
	| join_relation
	| (table_name|query|relation) [sample] [AS alias]
	: VALUES (expressions)[, (expressions), ...]
			[AS (column_name[, column_name, ...])]

expressions:
	: expression[, expression, ...]

sort_expressions:
	: expression [ASC|DESC][, expression [ASC|DESC], ...]
```


从一个或多个 `关系(relation)` 中输出结果， `关系(relation)` 可以是现有的表，2 张表连接查询的结果，或子查询。

> relation 通常可以理解为一组结果

**ALL**

查询所有匹配的数据（包含重复值），此属性默认指定

**DISTINCT**

查询所有匹配的数据，结果中去除重复值

**WHERE**

按谓词过滤行

**HAVING**

按谓词过滤分组结果

**ORDER BY**

对一组表达式加强总排序（对总的查询结果排序）。默认排序方向是升序。 `ORDER BY` 不能与 `SORT BY` 、 `CLUSTER BY` 或 `DISTRIBUTE BY` 一起使用

**DISTRIBUTE BY**

基于一组表达式，在 `关系` 中，重新分配行。具有相同表达式值的行将被散列到同一个 worker。 `DISTRIBUTE BY` 不能与 `ORDER BY` 、 `CLUSTER BY` 一起使用

**SORT BY**

对每个分区中的一组表达式进行排序。默认排序方向是升序。 `SORT BY` 不能与 `ORDER BY` 、 `CLUSTER BY` 一起使用

**CLUSTER BY**

根据一组表达式重新分配 `关系` 中的行，并根据表达式按行升序对行进行排序，可以被认为是 `DISTRIBUTE BY` 和 `SORT BY` 的简写，其中所有表达式按升序排序。 `CLUSTER BY` 不能与 `ORDER BY` 、 `DISTRIBUTE BY` 或 `SORT BY` 一起使用

**WINDOW**

为窗口分配标识符。请参见<<WindowFunctions,窗口函数>>。

**LIMIT**

限制返回的行数

**VALUES**

明确指定值，而不是从 `关系` 中读取它们。

**Example**

```sql
SELECT * FROM boxes
SELECT width, length FROM boxes WHERE height=3
SELECT DISTINCT width, length FROM boxes WHERE height=3 LIMIT 2
SELECT * FROM VALUES (1, 2, 3) AS (width, length, height)
SELECT * FROM VALUES (1, 2, 3), (2, 3, 4) AS (width, length, height)
SELECT * FROM boxes ORDER BY width
SELECT * FROM boxes DISTRIBUTE BY width SORT BY width
SELECT * FROM boxes CLUSTER BY length
```

## Sampling

```sql
sample:
	| TABLESAMPLE ((integer_expression | decimal_expression) PERCENT)
	: TABLESAMPLE (integer_expression ROWS)
```

对输入数据进行采样。可以用百分比（必须在 0 到 100 之间）或固定数量的输入行来表示。

**Example**

```sql
SELECT * FROM boxes TABLESAMPLE (3 ROWS)
SELECT * FROM boxes TABLESAMPLE (25 PERCENT)
```

## Joins

```sql
join_relation:
	| relation join_type JOIN relation (ON boolean_expression | USING (column_name[, column_name, ...]))
	: relation NATURAL join_type JOIN relation
join_type:
	| INNER
	| LEFT SEMI
	| (LEFT|RIGHT|FULL) [OUTER]
	: [LEFT] ANTI
```

**INNER JOIN**

从匹配的两个关系中选择所有行

**OUTER JOIN**

从两个关系中选择所有行，未匹配的一侧填充空值（NULL)

**LEFT SEMI JOIN**

以左表为准，在右表中查找匹配的记录，如果查找成功，则仅返回左边的记录，否则返回 NULL

**LEFT ANTI JOIN**

与 `LEFT SEMI JOIN` 相反，是以左表为准，在右表中查找匹配的记录，如果查找成功，则返回 NULL，否则仅返回左边的记录

**Example**

```sql
SELECT * FROM boxes INNER JOIN rectangles ON boxes.width = rectangles.width
SELECT * FROM boxes FULL OUTER JOIN rectangles USING (width, length)
SELECT * FROM boxes NATURAL JOIN rectangles
```

## Lateral View

```sql
lateral_view:
	: LATERAL VIEW [OUTER] function_name (expressions)
			table_name [AS (column_name[, column_name, ...])]
```

使用表生成函数为每个输入行生成零个或多个输出行，与 `LATERAL VIEW` 一起使用的最常见的内置函数是[explode]，二者配合实现数据的行转列、列转行的计算，可参考[HIVE 中关于 collect_set 与 explode 函数妙用](https://blog.csdn.net/sinat_29508201/article/details/48138207)
这篇文章理解

**LATERAL VIEW OUTER**

在函数返回零行，也会生成一个具有空值的行。

**Example**

```sql
SELECT * FROM boxes LATERAL VIEW explode(Array(1, 2, 3)) my_view
SELECT name, my_view.grade FROM students LATERAL VIEW OUTER explode(grades) my_view AS grade

-- 假设表muyunscoreSet内的2个字段为：nu(INT), score_set(ARRAY<INT>)，表内有2条数据：
-- |----nu----|----score_set----|
-- | 1        |      [2, 3]     |
-- | 1        |      [2, 4]     |
-- 执行如下SQL后
select * from muyunscoreSet lateral view explode(score_set) xxx as score;

-- 返回的数据为：
-- |----nu----|----score_set----|----score----|
-- | 1        |      [2, 3]     |      2      |
-- | 1        |      [2, 3]     |      3      |
-- | 1        |      [2, 4]     |      2      |
-- | 1        |      [2, 4]     |      4      |
-- 可实现将array类型的字段的行转列
```

## Aggregation

```sql
aggregation:
	: GROUP BY expressions [(WITH ROLLUP | WITH CUBE | GROUPING SETS (expressions))]
```

使用一个或多个聚合函数对一组表达式进行分组。常见的内置聚合函数包括 `count`, `avg`, `min`, `max`, `sum`

**ROLLUP**

在指定表达式的每个层次级别创建分组集，ROLLUP 是对 `GROUPING SETS` 的扩展

例如， `GROUP BY a，b，c WITH ROLLUP` 等同于 `GROUP BY a，b，c GROUPING SETS（（a，b，c），（a，b），（a），（））` 。分组集的总数将是 N + 1，其中 N 是组表达式的数量

**CUBE**

为每一种可能的组合生成 `grouping sets` ，ROLLUP 是对 `GROUPING SETS` 的扩展

例如，GROUP BY a，b，c WITH CUBE 等同于 GROUP BY a，b，c GROUPING SETS（（a，b，c），（a，b），（b，c），（a，c） ，（a），（b），（c），（））。分组集的总数将是 2^N，其中 N 是 group 的表达式的数量

**GROUPING SETS**

对 `grouping sets` 指定的表达式，分别执行 group by，并将结果进行 `union` 。例如， `GROUP BY x，y GROUPING SETS（x，y）` 等于 GROUP BY x 与 GROUP BY y 联合的结果。

**Example**

```sql
SELECT height, COUNT(*) AS num_rows FROM boxes GROUP BY height
SELECT width, AVG(length) AS average_length FROM boxes GROUP BY width
SELECT width, length, height FROM boxes GROUP BY width, length, height WITH ROLLUP
SELECT width, length, avg(height) FROM boxes GROUP BY width, length GROUPING SETS (width, length)
```

## Window Functions

```sql
window_expression:
	: expression OVER window_spec

named_window:
	: window_identifier AS window_spec

window_spec:
	| window_identifier
	: ((PARTITION|DISTRIBUTE) BY expressions
			[(ORDER|SORT) BY sort_expressions] [window_frame])

window_frame:
	| (RANGE|ROWS) frame_bound
	: (RANGE|ROWS) BETWEEN frame_bound AND frame_bound

frame_bound:
	| CURRENT ROW
	| UNBOUNDED (PRECEDING|FOLLOWING)
	: expression (PRECEDING|FOLLOWING)
```

计算一系列输入行的结果。使用 OVER 关键字指定窗口表达式，后面跟着窗口的标识符（window_identifier）（使用 WINDOW 关键字定义）或窗口的规格

**PARTITION BY**

指定哪些行将位于同一分区中，DISTRIBUTE BY 为别名。

**ORDER BY**

指定窗口分区中的行如何排序，SORT BY 为别名

**RANGE bound**

根据表达式的值范围来确定窗口的范围

**ROWS bound**

根据当前行的前面或后面的行数来确定窗口的范围

**CURRENT ROW**

使用当前行作为窗口范围

**UNBOUNDED**

使用负无穷大作为下限范围，或无穷大作为上限范围

**PRECEDING**

如果与 `RANGE bound` 一起使用，则定义值范围的下限

如果与 `ROWS bound` 绑定一起使用，则确定在当前行之前，需要保留在窗口中的行数

**FOLLOWING**

与 `PRECEDING` 相反，如果与 `RANGE bound` 绑定一起使用，则定义值范围的上限

如果与 `ROWS bound` 绑定一起使用，则确定在当前行之后，需要保留在窗口中的行数
=======
# Select
## Select

```sql
SELECT [hints, ...] [ALL|DISTINCT] named_expression[, named_expression, ...]
	FROM relation[, relation, ...]
	[lateral_view[, lateral_view, ...]]
	[WHERE boolean_expression]
	[aggregation [HAVING boolean_expression]]
	[ORDER BY sort_expressions]
	[CLUSTER BY expressions]
	[DISTRIBUTE BY expressions]
	[SORT BY sort_expressions]
	[WINDOW named_window[, WINDOW named_window, ...]]
	[LIMIT num_rows]

named_expression:
	: expression [AS alias]

relation:
	| join_relation
	| (table_name|query|relation) [sample] [AS alias]
	: VALUES (expressions)[, (expressions), ...]
			[AS (column_name[, column_name, ...])]

expressions:
	: expression[, expression, ...]

sort_expressions:
	: expression [ASC|DESC][, expression [ASC|DESC], ...]
```


从一个或多个 `关系(relation)` 中输出结果， `关系(relation)` 可以是现有的表，2 张表连接查询的结果，或子查询。

> relation 通常可以理解为一组结果

**ALL**

查询所有匹配的数据（包含重复值），此属性默认指定

**DISTINCT**

查询所有匹配的数据，结果中去除重复值

**WHERE**

按谓词过滤行

**HAVING**

按谓词过滤分组结果

**ORDER BY**

对一组表达式加强总排序（对总的查询结果排序）。默认排序方向是升序。 `ORDER BY` 不能与 `SORT BY` 、 `CLUSTER BY` 或 `DISTRIBUTE BY` 一起使用

**DISTRIBUTE BY**

基于一组表达式，在 `关系` 中，重新分配行。具有相同表达式值的行将被散列到同一个 worker。 `DISTRIBUTE BY` 不能与 `ORDER BY` 、 `CLUSTER BY` 一起使用

**SORT BY**

对每个分区中的一组表达式进行排序。默认排序方向是升序。 `SORT BY` 不能与 `ORDER BY` 、 `CLUSTER BY` 一起使用

**CLUSTER BY**

根据一组表达式重新分配 `关系` 中的行，并根据表达式按行升序对行进行排序，可以被认为是 `DISTRIBUTE BY` 和 `SORT BY` 的简写，其中所有表达式按升序排序。 `CLUSTER BY` 不能与 `ORDER BY` 、 `DISTRIBUTE BY` 或 `SORT BY` 一起使用

**WINDOW**

为窗口分配标识符。请参见<<WindowFunctions,窗口函数>>。

**LIMIT**

限制返回的行数

**VALUES**

明确指定值，而不是从 `关系` 中读取它们。

**Example**

```sql
SELECT * FROM boxes
SELECT width, length FROM boxes WHERE height=3
SELECT DISTINCT width, length FROM boxes WHERE height=3 LIMIT 2
SELECT * FROM VALUES (1, 2, 3) AS (width, length, height)
SELECT * FROM VALUES (1, 2, 3), (2, 3, 4) AS (width, length, height)
SELECT * FROM boxes ORDER BY width
SELECT * FROM boxes DISTRIBUTE BY width SORT BY width
SELECT * FROM boxes CLUSTER BY length
```

## Sampling

```sql
sample:
	| TABLESAMPLE ((integer_expression | decimal_expression) PERCENT)
	: TABLESAMPLE (integer_expression ROWS)
```

对输入数据进行采样。可以用百分比（必须在 0 到 100 之间）或固定数量的输入行来表示。

**Example**

```sql
SELECT * FROM boxes TABLESAMPLE (3 ROWS)
SELECT * FROM boxes TABLESAMPLE (25 PERCENT)
```

## Joins

```sql
join_relation:
	| relation join_type JOIN relation (ON boolean_expression | USING (column_name[, column_name, ...]))
	: relation NATURAL join_type JOIN relation
join_type:
	| INNER
	| LEFT SEMI
	| (LEFT|RIGHT|FULL) [OUTER]
	: [LEFT] ANTI
```

**INNER JOIN**

从匹配的两个关系中选择所有行

**OUTER JOIN**

从两个关系中选择所有行，未匹配的一侧填充空值（NULL)

**LEFT SEMI JOIN**

以左表为准，在右表中查找匹配的记录，如果查找成功，则仅返回左边的记录，否则返回 NULL

**LEFT ANTI JOIN**

与 `LEFT SEMI JOIN` 相反，是以左表为准，在右表中查找匹配的记录，如果查找成功，则返回 NULL，否则仅返回左边的记录

**Example**

```sql
SELECT * FROM boxes INNER JOIN rectangles ON boxes.width = rectangles.width
SELECT * FROM boxes FULL OUTER JOIN rectangles USING (width, length)
SELECT * FROM boxes NATURAL JOIN rectangles
```

## Lateral View

```sql
lateral_view:
	: LATERAL VIEW [OUTER] function_name (expressions)
			table_name [AS (column_name[, column_name, ...])]
```

使用表生成函数为每个输入行生成零个或多个输出行，与 `LATERAL VIEW` 一起使用的最常见的内置函数是[explode]，二者配合实现数据的行转列、列转行的计算，可参考[HIVE 中关于 collect_set 与 explode 函数妙用](https://blog.csdn.net/sinat_29508201/article/details/48138207)
这篇文章理解

**LATERAL VIEW OUTER**

在函数返回零行，也会生成一个具有空值的行。

**Example**

```sql
SELECT * FROM boxes LATERAL VIEW explode(Array(1, 2, 3)) my_view
SELECT name, my_view.grade FROM students LATERAL VIEW OUTER explode(grades) my_view AS grade

-- 假设表muyunscoreSet内的2个字段为：nu(INT), score_set(ARRAY<INT>)，表内有2条数据：
-- |----nu----|----score_set----|
-- | 1        |      [2, 3]     |
-- | 1        |      [2, 4]     |
-- 执行如下SQL后
select * from muyunscoreSet lateral view explode(score_set) xxx as score;

-- 返回的数据为：
-- |----nu----|----score_set----|----score----|
-- | 1        |      [2, 3]     |      2      |
-- | 1        |      [2, 3]     |      3      |
-- | 1        |      [2, 4]     |      2      |
-- | 1        |      [2, 4]     |      4      |
-- 可实现将array类型的字段的行转列
```

## Aggregation

```sql
aggregation:
	: GROUP BY expressions [(WITH ROLLUP | WITH CUBE | GROUPING SETS (expressions))]
```

使用一个或多个聚合函数对一组表达式进行分组。常见的内置聚合函数包括 `count`, `avg`, `min`, `max`, `sum`

**ROLLUP**

在指定表达式的每个层次级别创建分组集，ROLLUP 是对 `GROUPING SETS` 的扩展

例如， `GROUP BY a，b，c WITH ROLLUP` 等同于 `GROUP BY a，b，c GROUPING SETS（（a，b，c），（a，b），（a），（））` 。分组集的总数将是 N + 1，其中 N 是组表达式的数量

**CUBE**

为每一种可能的组合生成 `grouping sets` ，ROLLUP 是对 `GROUPING SETS` 的扩展

例如，GROUP BY a，b，c WITH CUBE 等同于 GROUP BY a，b，c GROUPING SETS（（a，b，c），（a，b），（b，c），（a，c） ，（a），（b），（c），（））。分组集的总数将是 2^N，其中 N 是 group 的表达式的数量

**GROUPING SETS**

对 `grouping sets` 指定的表达式，分别执行 group by，并将结果进行 `union` 。例如， `GROUP BY x，y GROUPING SETS（x，y）` 等于 GROUP BY x 与 GROUP BY y 联合的结果。

**Example**

```sql
SELECT height, COUNT(*) AS num_rows FROM boxes GROUP BY height
SELECT width, AVG(length) AS average_length FROM boxes GROUP BY width
SELECT width, length, height FROM boxes GROUP BY width, length, height WITH ROLLUP
SELECT width, length, avg(height) FROM boxes GROUP BY width, length GROUPING SETS (width, length)
```

## Window Functions

```sql
window_expression:
	: expression OVER window_spec

named_window:
	: window_identifier AS window_spec

window_spec:
	| window_identifier
	: ((PARTITION|DISTRIBUTE) BY expressions
			[(ORDER|SORT) BY sort_expressions] [window_frame])

window_frame:
	| (RANGE|ROWS) frame_bound
	: (RANGE|ROWS) BETWEEN frame_bound AND frame_bound

frame_bound:
	| CURRENT ROW
	| UNBOUNDED (PRECEDING|FOLLOWING)
	: expression (PRECEDING|FOLLOWING)
```

计算一系列输入行的结果。使用 OVER 关键字指定窗口表达式，后面跟着窗口的标识符（window_identifier）（使用 WINDOW 关键字定义）或窗口的规格

**PARTITION BY**

指定哪些行将位于同一分区中，DISTRIBUTE BY 为别名。

**ORDER BY**

指定窗口分区中的行如何排序，SORT BY 为别名

**RANGE bound**

根据表达式的值范围来确定窗口的范围

**ROWS bound**

根据当前行的前面或后面的行数来确定窗口的范围

**CURRENT ROW**

使用当前行作为窗口范围

**UNBOUNDED**

使用负无穷大作为下限范围，或无穷大作为上限范围

**PRECEDING**

如果与 `RANGE bound` 一起使用，则定义值范围的下限

如果与 `ROWS bound` 绑定一起使用，则确定在当前行之前，需要保留在窗口中的行数

**FOLLOWING**

与 `PRECEDING` 相反，如果与 `RANGE bound` 绑定一起使用，则定义值范围的上限

如果与 `ROWS bound` 绑定一起使用，则确定在当前行之后，需要保留在窗口中的行数
>>>>>>> dev
