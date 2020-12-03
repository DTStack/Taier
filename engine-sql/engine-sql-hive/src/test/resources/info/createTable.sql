create TEMPORARY table IF NOT EXISTS test.chener(
 p1 int not nulll default 100 comment 'id',
 p2 string UNIQUE default 'ppp'
) comment 'table comment'
PARTITIONED BY (pt int comment 'par')
CLUSTERED BY(p1) SORTED BY (p1 ASC) INTO 3 BUCKETS
SKEWED BY(p1,p2) on ((1,'haha'),(2,'lala')) STORED AS DIRECTORIES
ROW FORMAT DELIMITED FIELDS TERMINATED BY char
STORED AS orc
LOCATION 'lcao'
TBLPROPERTIES (comment='asd')
as select p1,p2 from test.chen1