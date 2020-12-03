create TEMPORARY table IF NOT EXISTS test.chener(
 p1 int not nulll default 100 comment 'id',
 p2 string UNIQUE default 'ppp'
) comment 'table comment'
PARTITIONED BY (pt int comment 'par')