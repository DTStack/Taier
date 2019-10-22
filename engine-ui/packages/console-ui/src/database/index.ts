import LocalIndexedDB from 'utils/indexedDB';

/**
 * 离线计算工作台
 */
export const offlineWorkbenchDB = new LocalIndexedDB('insight.dtstack.com', 1, 'offline_workbench');
/**
 * 数据科学工作台
 */
export const dataScienceWorkbenchDB = new LocalIndexedDB('insight.dtstack.com', 1, 'science_workbench');
