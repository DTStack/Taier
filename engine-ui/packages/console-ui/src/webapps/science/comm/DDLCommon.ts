export const DdlPlaceholder =
`CREATE TABLE IF NOT EXISTS table_name(
    col_name data_type COMMENT 'col_comment'
) COMMENT 'table_comment'
    PARTITIONED BY (
col_name data_type COMMENT 'col_comment'
    ) lifecycle 90;`

export const DdlIdePlaceholder =
`CREATE TABLE employee ( 
    eid int, 
    name String,
    salary String, 
    destination String)
    row format delimited fields terminated by ',' 
    STORED AS TEXTFILE
    lifecycle 10`;

export const DdlPlaceholderAnly =
`CREATE TABLE IF NOT EXISTS tablename
( col_name1 data_type COMMENT 'col_comment')
PARTITIONED BY (col_name2 data_type COMMENT 'col_comment')
STORED AS carbondata
LIFECYCLE 90;`
