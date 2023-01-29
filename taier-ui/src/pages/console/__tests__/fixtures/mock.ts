export const getAllCluster = {
	code: 1,
	data: [
		{
			id: -1,
			gmtCreate: 1643336761000,
			gmtModified: 1672797889000,
			isDeleted: 0,
			clusterName: 'default',
			clusterId: -1,
			canModifyMetadata: true,
		},
		{
			id: 1,
			gmtCreate: 1669898140000,
			gmtModified: 1669898140000,
			isDeleted: 0,
			clusterName: 'dev',
			clusterId: 9,
			canModifyMetadata: true,
		},
	],
};

export const getNodeAddressSelect = {
	code: 1,
	data: ['127.0.0.1:8090'],
};

export const getClusterDetail = {
	code: 1,
	data: [
		{
			lackingWaitTime: '259小时27分钟17秒',
			restart: 3,
			jobResource: 'spark_sql_default_default_batch_Yarn',
			lacking: 4,
			priorityJobSize: 0,
			priority: 2,
			submittedWaitTime: '',
			dbJobSize: 0,
			restartJobSize: 0,
			submitted: 5,
			priorityWaitTime: '',
			restartWaitTime: '',
			lackingJobSize: 25,
			dbWaitTime: '',
			db: 1,
			submittedJobSize: 0,
		},
		{
			lackingWaitTime: '235小时26分钟31秒',
			restart: 3,
			jobResource: 'shell_default_default_batch_',
			lacking: 4,
			priorityJobSize: 0,
			priority: 2,
			submittedWaitTime: '',
			dbJobSize: 0,
			restartJobSize: 0,
			submitted: 5,
			priorityWaitTime: '',
			restartWaitTime: '',
			lackingJobSize: 7,
			dbWaitTime: '',
			db: 1,
			submittedJobSize: 0,
		},
	],
};
