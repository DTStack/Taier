export const DDL_PLACEHOLDER =
`CREATE TABLE IF NOT EXISTS table_name(
    col_name data_type COMMENT 'col_comment'
) COMMENT 'table_comment'
    PARTITIONED BY (
col_name data_type COMMENT 'col_comment'
    ) lifecycle 90;`

export const DDL_IDE_PLACEHOLDER =
`CREATE TABLE employee ( 
    eid int, 
    name String,
    salary String, 
    destination String)
    STORED AS ORC
    lifecycle 10`;

export const LIBRA_DDL_IDE_PLACEHOLDER =
    `CREATE TABLE user ( 
        eid int, 
        name varChar,
        salary varChar, 
        gender int)
        STORED AS ORC
        lifecycle 10`;
