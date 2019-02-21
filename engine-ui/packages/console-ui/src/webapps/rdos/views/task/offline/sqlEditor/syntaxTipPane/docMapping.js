const docBasePath = '/public/rdos/docs/syntax';

const docsMapping = [
    {
        id: 'normal',
        name: '常用',
        children: [
            {
                id: 'normal_create_table',
                name: 'Create Table',
                file: `${docBasePath}/createTable.md`
            }, {
                id: 'normal_alter_table_partitions',
                name: 'Alter Table Partitions',
                file: `${docBasePath}/alterTable.md`
            }, {
                id: 'normal_select',
                name: 'Select',
                file: `${docBasePath}/createTable.md`
            }, {
                id: 'normal_insert',
                name: 'Insert',
                file: `${docBasePath}/createTable.md`
            }
        ]
    }, {
        id: 'ddl',
        name: 'DDL',
        children: [
            {
                id: 'ddl_create_table',
                name: 'Create Table',
                file: ''
            }, {
                id: 'ddl_alter_table',
                name: 'Alter Table'
            }, {
                id: 'ddl_select',
                name: 'Select'
            }, {
                id: 'ddl_insert',
                name: 'Insert'
            }
        ]
    }, {
        id: 'dml',
        name: 'DML',
        children: [
            {
                id: 'dml_select',
                name: 'Insert',
                file: ''
            }, {
                id: 'dml_insert',
                name: 'Insert'
            }
        ]
    }, {
        id: 'show',
        name: 'SHOW',
        children: [
            {
                id: 'show_create_table',
                name: 'Show Tables',
                file: ''
            }, {
                id: 'show_alter_table',
                name: 'Show Columns'
            }, {
                id: 'show_select',
                name: 'Show Partitions'
            }, {
                id: 'show_insert',
                name: 'Show Create Table'
            }
        ]
    }, {
        id: 'others',
        name: 'Others',
        children: [
            {
                id: 'others_desc_table',
                name: 'Describe Table',
                file: ''
            }, {
                id: 'others_explain',
                name: 'Explain'
            }, {
                id: 'others_select',
                name: 'Set'
            }
        ]
    }
];

export default docsMapping;
