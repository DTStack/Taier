export const DDL_placeholder = 
`CREATE TABLE IF NOT EXISTS 'table_name'(
    'col_name' data_type COMMENT 'col_comment'
) COMMENT 'table_comment'
    PARTITIONED BY (
'col_name' data_type COMMENT 'col_comment'
    ) lifecycle 90;`