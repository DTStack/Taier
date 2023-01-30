import { CREATE_MODEL_TYPE, TASK_TYPE_ENUM } from '@/constant';

export const scriptSyncTab = {
    data: {
        taskType: TASK_TYPE_ENUM.SYNC,
        createModel: CREATE_MODEL_TYPE.SCRIPT,
        'test|flink': 1,
        sourceMap: {
            sourceId: 23,
            rdbmsDaType: 2,
            halfStructureDaType: 0,
            sourceList: [],
            isFirstLineHeader: true,
            column: [
                {
                    index: 0,
                    type: 'STRING',
                },
                {
                    index: 1,
                    type: 'STRING',
                },
            ],
            type: 9,
            encoding: 'utf-8',
            fieldDelimiter: ',',
            path: '/home/test.txt',
            syncModel: 0,
            fileType: 'txt',
        },
        targetMap: {
            sourceId: 17,
            column: [
                {
                    part: false,
                    comment: '',
                    isPart: false,
                    type: 'INT',
                    key: 'string',
                },
                {
                    part: false,
                    comment: '',
                    isPart: false,
                    type: 'INT',
                    key: 'age',
                },
            ],
            type: 1,
            table: 'ftp_new',
        },
        settingMap: {
            channel: '1',
            speed: '-1',
        },
    },
};

export const guideSyncTabInWorkflow = {
    data: {
        flowId: 123,
        id: 'workflow__111',
        taskType: TASK_TYPE_ENUM.SYNC,
        createModel: CREATE_MODEL_TYPE.GUIDE,
        'test|flink': 1,
        sourceMap: {
            sourceId: 23,
            rdbmsDaType: 2,
            halfStructureDaType: 0,
            sourceList: [],
            isFirstLineHeader: true,
            column: [
                {
                    index: 0,
                    type: 'STRING',
                },
                {
                    index: 1,
                    type: 'STRING',
                },
            ],
            type: 9,
            encoding: 'utf-8',
            fieldDelimiter: ',',
            path: '/home/test.txt',
            syncModel: 0,
            fileType: 'txt',
        },
        targetMap: {
            sourceId: 17,
            column: [
                {
                    part: false,
                    comment: '',
                    isPart: false,
                    type: 'INT',
                    key: 'string',
                },
                {
                    part: false,
                    comment: '',
                    isPart: false,
                    type: 'INT',
                    key: 'age',
                },
            ],
            type: 1,
            table: 'ftp_new',
        },
        settingMap: {
            channel: '1',
            speed: '-1',
        },
    },
};

export const guideFlinkSQL = {
    data: {
        componentVersion: '1.12',
        taskType: TASK_TYPE_ENUM.SQL,
        createModel: CREATE_MODEL_TYPE.GUIDE,
        source: [
            {
                type: 37,
                sourceId: '19',
                topic: 'test_topic_name_gcwfybte',
                charset: 'utf-8',
                table: 'test',
                timeType: 1,
                timeTypeArr: [1],
                timeZone: 'Asia/Shanghai',
                offset: 0,
                offsetUnit: 'SECOND',
                columnsText: 'id int',
                parallelism: 1,
                offsetReset: 'latest',
                sourceDataType: 'dt_nest',
            },
        ],
        sink: [
            {
                type: 1,
                columns: [
                    {
                        column: 'i1',
                        type: 'VARBINARY',
                    },
                ],
                parallelism: 1,
                bulkFlushMaxActions: 100,
                batchWaitInterval: 1000,
                batchSize: 100,
                enableKeyPartitions: false,
                updateMode: 'append',
                allReplace: 'false',
                sourceId: 9,
                table: 'binary_test',
                tableName: 'test',
            },
        ],
        side: [
            {
                type: 1,
                columns: [
                    {
                        column: 'i1',
                        type: 'VARBINARY',
                    },
                ],
                sourceId: 9,
                table: 'binary_test',
                tableName: 'test',
                parallelism: 1,
                cache: 'LRU',
                cacheSize: 10000,
                cacheTTLMs: 60000,
                asyncPoolSize: 5,
            },
        ],
    },
};

export const guideAcquisition = {
    data: {
        taskType: 6,
        createModel: 0,
        sourceMap: {
            type: 1,
            table: ['binary_test'],
            sourceId: 17,
            collectType: 0,
            cat: [1, 2, 3],
            pavingData: false,
            rdbmsDaType: 1,
            codec: 'plain',
            qos: 2,
            isCleanSession: true,
            parse: 'text',
            decoder: 'FixedLength',
            codecType: 'text',
            collectPoint: 'taskRun',
            requestMode: 'post',
            decode: 'text',
            protocol: 'http',
            temporary: false,
            slotConfig: 1,
            mode: 'group-offsets',
            multipleTable: false,
            allTable: false,
            distributeTable: [{ name: 'test', tables: ['a'] }],
        },
        targetMap: {
            sourceId: 19,
            topic: 'test',
            isCleanSession: true,
            qos: 2,
            type: 37,
            dataSequence: false,
        },
        settingMap: {
            speed: -1,
            readerChannel: '1',
            writerChannel: 1,
        },
    },
};

export const virtualTask = {
    data: {
        taskType: -1,
        name: 'virtual',
        computeType: 1,
        taskDesc: '',
        id: 'workflow__abc',
        flowId: 1,
    },
};
