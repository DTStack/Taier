const data = {
    sourceMap: {
        sourceId: 1,
        name: 'dt_rdos',
        type: {
            type: 1,
            where: 'whereid>3',
            splitPK: 'id',
            table: 'rdos_batch_alarm_record',

        },
        column: [{
                key: 'id',
                type: 'long'
            },
            {
                key: 'alarm_id',
                type: 'long'
            },
            {
                key: 'alarm_content',
                type: 'string'
            },
            {
                key: 'project_id',
                type: 'long'
            },
            {
                key: 'tenant_id',
                type: 'int'
            },
            {
                key: 'gmt_create',
                type: 'date'
            },
            {
                key: 'gmt_modified',
                type: 'date'
            },
            {
                key: 'is_deleted',
                type: 'int'
            }
        ]
    },
    targetMap: {
        sourceId: 1,
        name: 'rdos',
        type: {
            type: 2,
            table: 'rdos_stream_alarm_record',
            writeMode: "insert",
            preSql: 'yyy',
            postSql: '学习学习'
        },
        column: [{
                key: 'id',
                type: 'long'
            },
            
            {
                key: 'tenant_id',
                type: 'int'
            },
            {
                key: 'gmt_create',
                type: 'date'
            },
            {
                key: 'alarm_id',
                type: 'long'
            },
            {
                key: 'gmt_modified',
                type: 'date'
            },
            {
                key: 'is_deleted',
                type: 'int'
            },
            {
                key: 'alarm_content',
                type: 'string'
            },
            {
                key: 'project_id',
                type: 'long'
            }
        ]
    },
    keymap: {
        source: [
            "id",
            "alarm_id",            
        ],
        target: [
            "id",
            "alarm_content"
        ]
    },
    setting: {
        'channel': 1,
        'speed': 2,
        'record': 500,
        'percentage': 20
    }
};

export default data;