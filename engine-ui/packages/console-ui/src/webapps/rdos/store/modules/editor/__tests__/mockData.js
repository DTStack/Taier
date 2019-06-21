export let respWithoutPoll = {
    code: 1,
    data: {
        'result': [
            [
                'database',
                'tableName',
                'isTemporary'
            ],
            [
                'mufeng_0129',
                'ads_ct_ks_bank_card_info',
                false
            ]
        ],
        'jobId': null,
        'sqlText': 'show tables'
    },
    message: 'error-text',
    space: 348
};

export let errorResp = {
    code: 2,
    data: null,
    message: 'error-text',
    space: 348
};
export let respWithPoll = {
    code: 1,
    data: {
        'jobId': 'dtstack_jobid',
        'sqlText': 'show tables'
    },
    message: 'error-text',
    space: 348
};
export let pollRespCollection = [
    {
        code: 1,
        data: { result: null, status: 16 },
        message: '1',
        space: 69
    },
    {
        code: 1,
        data: { result: null, status: 16 },
        message: null,
        space: 69
    },
    {
        code: 1,
        data: { result: null, status: 4 },
        message: '2',
        space: 403
    },
    {
        code: 1,
        data: {
            download: '/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=e5edd8c0&taskType=0',
            msg: '',
            result: [
                ['count(1)'],
                ['0']
            ],
            status: 5
        },
        message: null,
        space: 924
    }
];
