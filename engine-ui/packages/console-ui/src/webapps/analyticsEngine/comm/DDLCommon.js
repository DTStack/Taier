export const DDL_placeholder =
`CREATE TABLE IF NOT EXISTS table_name(
    col_name data_type COMMENT 'col_comment'
) COMMENT 'table_comment'
    PARTITIONED BY (
col_name data_type COMMENT 'col_comment'
    ) lifecycle 90;`

export const DDL_ide_placeholder =
`CREATE TABLE employee ( 
    eid int, 
    name String,
    salary String, 
    destination String)
    STORED AS TEXTFILE
    lifecycle 10`;

export const DDL_placeholder_anly =
`CREATE TABLE IF NOT EXISTS tablename
( col_name1 data_type COMMENT 'col_comment')
PARTITIONED BY (col_name2 data_type COMMENT 'col_comment')
STORED AS carbondata
LIFECYCLE 90;`
