import api from '@/api';
import { DATA_SOURCE_ENUM, FLINK_VERSIONS, KAFKA_DATA_TYPE, SOURCE_TIME_TYPE } from '@/constant';
import type { IOfflineTaskProps } from '@/interface';
import { isEditing } from '@/pages/editor/workflow';
import molecule from '@dtinsight/molecule';
import { waitFor } from '@testing-library/react';
import { message } from 'antd';
import { catalogueService, rightBarService } from '..';
import taskSaveService, { SaveEventKind } from '../taskSaveService';
import viewStoreService from '../viewStoreService';
import {
	guideAcquisition,
	guideFlinkSQL,
	guideSyncTabInWorkflow,
	scriptSyncTab,
	virtualTask,
} from './fixtures/taskSaveService';

jest.mock('@/api');
jest.mock('@/services/viewStoreService', () => ({
	getViewStorage: jest.fn(),
	emiStorageChange: jest.fn(),
}));
jest.mock('@/services', () => ({
	catalogueService: {
		onUpdate: jest.fn(),
		loadTreeNode: jest.fn(),
	},
    breadcrumbService:{
        getBreadcrumb: jest.fn()
    },
	rightBarService: {
		getForm: jest.fn(() => ({
			componentForm: {
				validateFields: jest.fn(),
			},
		})),
	},
}));
jest.mock('antd', () => ({
	message: {
		success: jest.fn(),
		error: jest.fn(),
	},
}));

describe('Test TaskSaveService', () => {
	it('Should generate rules for source in flink', () => {
		expect(
			taskSaveService.generateValidDesSource(
				{
					type: DATA_SOURCE_ENUM.KAFKA,
					sourceDataType: KAFKA_DATA_TYPE.TYPE_AVRO,
					timeTypeArr: [SOURCE_TIME_TYPE.EVENT_TIME],
				} as IOfflineTaskProps['source'][number],
				FLINK_VERSIONS.FLINK_1_12,
			),
		).toMatchSnapshot();
	});

	it('Should transform tab data', () => {
		expect(
			taskSaveService.transformTabDataToParams({
				value: 'this is value',
				componentVersion: FLINK_VERSIONS.FLINK_1_12,
				source: [
					{
						timeTypeArr: [1],
					},
				],
			} as unknown as IOfflineTaskProps),
		).toEqual({
			value: 'this is value',
			sqlText: 'this is value',
			componentVersion: FLINK_VERSIONS.FLINK_1_12,
			source: [
				{
					timeTypeArr: [1],
					procTime: 'proc_time',
				},
			],
		});
	});

	describe('Test save function', () => {
		beforeEach(() => {
			(message.error as jest.Mock).mockReset();
			(taskSaveService.emit as jest.Mock).mockReset();
			(api.saveTask as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {},
			});
			(api.addOfflineTask as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {},
			});
		});

		it('Should save sync tab with script mode', async () => {
			(api.saveOfflineJobData as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {},
			});
			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: scriptSyncTab,
				},
			}));

			// save script sync task at first time
			taskSaveService.save();

			await waitFor(() => {
				expect(api.saveOfflineJobData).toBeCalledTimes(1);
				expect(api.saveOfflineJobData).toBeCalledWith({
					computeType: 1,
					createModel: 1,
					preSave: true,
					settingMap: { channel: '1', speed: '-1' },
					sourceMap: {
						column: [
							{ index: 0, type: 'STRING' },
							{ index: 1, type: 'STRING' },
						],
						encoding: 'utf-8',
						fieldDelimiter: ',',
						fileType: 'txt',
						halfStructureDaType: 0,
						isFirstLineHeader: true,
						path: '/home/test.txt',
						rdbmsDaType: 2,
						sourceId: 23,
						sourceList: [],
						syncModel: 0,
						type: 9,
					},
					sqlText: '',
					targetMap: {
						column: [
							{ comment: '', isPart: false, key: 'string', part: false, type: 'INT' },
							{ comment: '', isPart: false, key: 'age', part: false, type: 'INT' },
						],
						sourceId: 17,
						table: 'ftp_new',
						type: 1,
					},
					taskType: 2,
					test: 1,
				});
				// emit onSaveTask event handler after saving
				expect(taskSaveService.emit).toBeCalledTimes(1);
				expect(taskSaveService.emit).toBeCalledWith(SaveEventKind.onSaveTask, {});
			});
		});

		it('Should save sync tab with guide mode', async () => {
			(api.saveOfflineJobData as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {},
			});

			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: guideSyncTabInWorkflow,
				},
			}));

			(molecule.folderTree.get as jest.Mock).mockReset().mockImplementation(() => ({
				data: {
					parentId: 1,
				},
			}));
			(taskSaveService.emit as jest.Mock)
				.mockReset()
				.mockImplementationOnce((_, ctx) => {
					ctx.stop();
				})
				.mockImplementation((type, ctx) => {
					if (type === SaveEventKind.onBeforeSave) {
						ctx.continue();
					}
				});

			// save guide sync task
			expect(taskSaveService.save()).rejects.toEqual(
				new Error('请检查数据同步任务是否填写正确'),
			);

			await waitFor(() => {
				expect(taskSaveService.emit).toBeCalledTimes(1);
				expect((taskSaveService.emit as jest.Mock).mock.calls[0][0]).toBe(
					SaveEventKind.onBeforeSave,
				);
			});

			expect(taskSaveService.save()).resolves.toEqual({
				code: 1,
				data: {},
			});

			await waitFor(() => {
				expect(taskSaveService.emit).toBeCalledTimes(3);
				expect(taskSaveService.emit).lastCalledWith(SaveEventKind.onSaveTask, {});
			});
		});

		it('Should check side failed in flinkSQL', async () => {
			const redisPrimaryKeyLost: typeof guideFlinkSQL = JSON.parse(
				JSON.stringify(guideFlinkSQL),
			);
			redisPrimaryKeyLost.data.side[0].type = 12;
			(redisPrimaryKeyLost.data.side[0] as any).columnsText = '123';

			(molecule.editor.getState as jest.Mock).mockReset().mockImplementationOnce(() => ({
				current: {
					tab: redisPrimaryKeyLost,
				},
			}));

			await expect(taskSaveService.save()).rejects.toEqual(
				new Error('维表1中的主键不能为空'),
			);
		});

		it('Should save flink SQL in guide mode', async () => {
			const failedGuideFlinkSQL: typeof guideFlinkSQL = JSON.parse(
				JSON.stringify(guideFlinkSQL),
			);
			failedGuideFlinkSQL.data.source[0].table = '';

			(molecule.editor.getState as jest.Mock)
				.mockReset()
				.mockImplementationOnce(() => ({
					current: {
						tab: failedGuideFlinkSQL,
					},
				}))
				.mockImplementation(() => ({
					current: {
						tab: guideFlinkSQL,
					},
				}));

			await expect(taskSaveService.save()).rejects.toEqual(undefined);

			expect(
				(message.error as jest.Mock).mock.calls.map((call) => call.join('\n')).join('\n'),
			).toBe('源表 1 : 请输入映射表名');

			await expect(taskSaveService.save()).resolves.toEqual({ code: 1, data: {} });

			expect(api.saveTask).toBeCalled();
			expect((api.saveTask as jest.Mock).mock.calls[0][0]).toMatchSnapshot();
			expect(taskSaveService.emit).toBeCalledWith(SaveEventKind.onSaveTask, {});
		});

		it('Should save flink SQL in script mode', async () => {
			const scriptFlinkSQL = JSON.parse(JSON.stringify(guideFlinkSQL));
			scriptFlinkSQL.data.createModel = 1;

			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: scriptFlinkSQL,
				},
			}));

			await expect(taskSaveService.save()).resolves.toEqual({ code: 1, data: {} });
			expect(api.saveTask).toBeCalled();
			expect((api.saveTask as jest.Mock).mock.calls[0][0]).toMatchSnapshot();
		});

		it('Should save DATA_ACQUISITION failed', async () => {
			(rightBarService.getForm as jest.Mock)
				.mockReset()
				.mockImplementationOnce(() => ({
					validateFields: jest.fn().mockRejectedValue(new Error('test')),
				}))
				.mockImplementation(() => ({
					validateFields: jest.fn().mockResolvedValue({}),
				}));

			const failedAcquisition: typeof guideAcquisition = JSON.parse(
				JSON.stringify(guideAcquisition),
			);
			failedAcquisition.data.sourceMap.pavingData = false;
			failedAcquisition.data.targetMap.type = 7;

			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: failedAcquisition,
				},
			}));

			// failed at first time since validateFields failed;
			await expect(taskSaveService.save()).rejects.toEqual(new Error('test'));

			// failed at second time
			await expect(taskSaveService.save()).rejects.toEqual(undefined);
			expect(message.error).toBeCalledWith('请勾选嵌套Json平铺后重试');
		});

		it('Should save DATA_ACQUISITION successfully', async () => {
			(rightBarService.getForm as jest.Mock).mockReset().mockImplementation(() => ({
				validateFields: jest.fn().mockResolvedValue({}),
			}));
			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: guideAcquisition,
				},
			}));

			await expect(taskSaveService.save()).resolves.toEqual({ code: 1, data: {} });

			expect(api.saveTask).toBeCalledTimes(1);
			expect((api.saveTask as jest.Mock).mock.calls[0][0]).toMatchSnapshot();
		});

		it('Should save virtual task', async () => {
			(molecule.folderTree.get as jest.Mock).mockReset().mockImplementation(() => ({
				data: {
					parentId: 1,
				},
			}));

			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: virtualTask,
				},
			}));

			await expect(taskSaveService.save()).resolves.toEqual({ code: 1, data: {} });

			expect(api.addOfflineTask).toBeCalledTimes(1);
			expect((api.addOfflineTask as jest.Mock).mock.calls[0][0]).toMatchSnapshot();

			expect((taskSaveService.emit as jest.Mock).mock.calls[0]).toEqual([
				SaveEventKind.onSaveTask,
				{},
			]);
		});

		it('Should save workflow task failed', async () => {
			(viewStoreService.getViewStorage as jest.Mock).mockReset().mockImplementation(() => ({
				cells: [
					{ edge: true },
					{
						vertex: true,
						value: {
							id: 'workflow__abc',
							nodePid: 1,
							[isEditing]: true,
						},
						setValue: jest.fn(),
					},
				],
			}));

			(api.addOfflineTask as jest.Mock).mockReset().mockResolvedValue({
				code: 0,
				data: {},
				message: 'test',
			});

			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: {
						data: {
							id: 1,
							taskType: 10,
						},
					},
				},
			}));

			await expect(taskSaveService.save()).rejects.toEqual(undefined);
			expect(message.error).lastCalledWith('test');
		});

		it('Should save workflow task successfully', async () => {
			(viewStoreService.getViewStorage as jest.Mock).mockReset().mockImplementation(() => ({
				cells: [
					{ edge: true, source: { value: { id: 1 } }, target: { value: { id: 2 } } },
					{
						vertex: true,
						value: {
							id: 'workflow__abc',
							nodePid: 1,
							[isEditing]: true,
						},
						setValue: jest.fn(),
					},
				],
			}));

			(api.getOfflineTaskByID as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {},
			});
			(api.addOfflineTask as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {
					id: 1,
				},
			});

			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: {
						data: {
							id: 1,
							taskType: 10,
						},
					},
				},
			}));

			(molecule.editor.isOpened as jest.Mock).mockReset().mockImplementation(() => true);
			(molecule.editor.updateTab as jest.Mock).mockReset();

			await expect(taskSaveService.save()).resolves.toEqual({ code: 1, data: { id: 1 } });
			expect(api.addOfflineTask).toBeCalledTimes(2);
			expect(api.addOfflineTask).lastCalledWith({
				id: 1,
				nodeMap: { '2': [1], workflow__abc: [] },
				taskType: 10,
			});
			expect((taskSaveService.emit as jest.Mock).mock.calls[0]).toEqual([
				SaveEventKind.onSaveTask,
				{ id: 1 },
			]);
		});

		it('Should save sparkMR successfully', async () => {
			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: {
						data: {
							id: 1,
							name: 'hadooooooopMR',
							resourceIdList: [7],
							mainClass: 'a.x',
							exeArgs: null,
							componentVersion: '2.1',
							nodePid: 1,
							taskType: 1,
							taskDesc: '',
							updateSource: false,
							preSave: false,
						},
					},
				},
			}));

			const original = taskSaveService.updateFolderAndTabAfterSave;
			taskSaveService.updateFolderAndTabAfterSave = jest.fn();

			await expect(taskSaveService.save()).resolves.toEqual({ code: 1, data: {} });

			expect((api.addOfflineTask as jest.Mock).mock.calls[0][0]).toMatchSnapshot();
			expect((taskSaveService.emit as jest.Mock).mock.calls[0]).toEqual([
				SaveEventKind.onSaveTask,
				{},
			]);
			taskSaveService.updateFolderAndTabAfterSave = original;
		});

		it('Should save sparkSQL successful', async () => {
			(molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
				current: {
					tab: {
						data: {
							id: 'workflow__abc',
							flowId: 1,
							value: 'test',
							nodePid: 1,
							taskType: 0,
						},
					},
				},
			}));

			(api.saveOfflineJobData as jest.Mock).mockReset().mockResolvedValue({
				code: 1,
				data: {},
			});

			await expect(taskSaveService.save()).resolves.toEqual({ code: 1, data: {} });

			expect((api.saveOfflineJobData as jest.Mock).mock.calls[0][0]).toMatchSnapshot();
			expect((taskSaveService.emit as jest.Mock).mock.calls[0]).toEqual([
				SaveEventKind.onSaveTask,
				{},
			]);
		});
	});

	it('Should support save specific tab', async () => {
		(api.addOfflineTask as jest.Mock).mockReset().mockResolvedValue({
			code: 1,
			data: {
				id: 1,
				nodePid: 2,
			},
		});

		(api.getOfflineTaskByID as jest.Mock).mockReset().mockResolvedValue({
			code: 1,
			data: {
				flowId: 2,
			},
		});

		(molecule.editor.isOpened as jest.Mock).mockReset().mockImplementation(() => true);
		(molecule.editor.updateTab as jest.Mock).mockReset();
		(viewStoreService.getViewStorage as jest.Mock).mockReset().mockImplementation(() => ({
			cells: [
				{
					vertex: true,
					value: {
						id: 1,
					},
					setValue: jest.fn(),
				},
			],
		}));
		(viewStoreService.emiStorageChange as jest.Mock).mockReset();

		await expect(
			taskSaveService.saveTab({
				id: 1,
				nodePid: 2,
			} as IOfflineTaskProps),
		).resolves.toEqual({ code: 1, data: { id: 1, nodePid: 2 } });

		expect((api.addOfflineTask as jest.Mock).mock.calls[0][0]).toMatchSnapshot();

		// set tab's status to saved
		expect(molecule.editor.updateTab).toBeCalledWith({
			id: '1',
			data: {
				flowId: 2,
			},
			status: undefined,
		});
	});

	it('Should update tab and folder after save', async () => {
		(molecule.folderTree.get as jest.Mock).mockReset().mockImplementation(() => ({}));
		(catalogueService.loadTreeNode as jest.Mock).mockReset();
		(molecule.editor.isOpened as jest.Mock).mockReset().mockImplementation(() => true);
		(molecule.editor.updateTab as jest.Mock).mockReset();
		(molecule.editor.getGroupIdByTab as jest.Mock).mockReset();
		(molecule.editor.closeTab as jest.Mock).mockReset();

		taskSaveService.updateFolderAndTabAfterSave(1, 2, 3, 'test', 'ttt');

		await waitFor(() => {
			expect(catalogueService.loadTreeNode).toBeCalledTimes(2);
			expect(molecule.editor.updateTab).toBeCalled();
			expect(molecule.editor.closeTab).toBeCalled();
		});
	});
});
