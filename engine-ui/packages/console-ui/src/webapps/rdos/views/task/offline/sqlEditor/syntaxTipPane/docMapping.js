const docBasePath = '/public/rdos/docs/sqlSyntax';

const docsMapping = [
    {
        id: 'normal',
        name: '常用',
        children: [
            {
                id: 'normal_create_table',
                name: 'Create Table',
                file: `${docBasePath}/DDL/CreateTable.md`
            }, {
                id: 'normal_alter_table_partitions',
                name: 'Alter Table Partitions',
                file: `${docBasePath}/DDL/AlterTablePartitions.md`
            }, {
                id: 'normal_select',
                name: 'Select',
                file: `${docBasePath}/DML/Select.md`
            }, {
                id: 'normal_insert',
                name: 'Insert',
                file: `${docBasePath}/DML/Insert.md`
            }
        ]
    }, {
        id: 'ddl',
        name: 'DDL',
        children: [
            {
                id: 'ddl_create_table',
                name: 'Create Table',
                file: `${docBasePath}/DDL/CreateTable.md`
            }, {
                id: 'ddl_alter_table',
                name: 'Alter Table',
                file: `${docBasePath}/DDL/AlterTable.md`
            }, {
                id: 'ddl_alter_table_partitions',
                name: 'Alter Table Partitions',
                file: `${docBasePath}/DDL/AlterTablePartitions.md`
            }, {
                id: 'ddl_truncate_table',
                name: 'Truncate Table',
                file: `${docBasePath}/DDL/TruncateTable.md`
            }, {
                id: 'ddl_drop_table',
                name: 'Drop Table',
                file: `${docBasePath}/DDL/DropTable.md`
            }
        ]
    }, {
        id: 'dml',
        name: 'DML',
        children: [
            {
                id: 'dml_select',
                name: 'Select',
                file: `${docBasePath}/DML/Select.md`
            }, {
                id: 'dml_insert',
                name: 'Insert',
                file: `${docBasePath}/DML/Insert.md`
            }
        ]
    }, {
        id: 'show',
        name: 'SHOW',
        children: [
            {
                id: 'show_tables',
                name: 'Show Tables',
                file: `${docBasePath}/SHOW/ShowTables.md`
            }, {
                id: 'show_columns',
                name: 'Show Columns',
                file: `${docBasePath}/SHOW/ShowColumns.md`
            }, {
                id: 'show_partitions',
                name: 'Show Partitions',
                file: `${docBasePath}/SHOW/ShowPartitions.md`
            }, {
                id: 'show_create_table',
                name: 'Show Create Table',
                file: `${docBasePath}/SHOW/ShowCreateTable.md`
            }
        ]
    }, {
        id: 'others',
        name: 'Others',
        children: [
            {
                id: 'others_desc_table',
                name: 'Describe Table',
                file: `${docBasePath}/Others/DescribeTable.md`
            }, {
                id: 'others_explain',
                name: 'Explain',
                file: `${docBasePath}/Others/Explain.md`
            }, {
                id: 'others_select',
                name: 'Set',
                file: `${docBasePath}/Others/Set.md`
            }
        ]
    }
];

export default docsMapping;
