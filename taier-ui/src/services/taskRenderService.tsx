import 'reflect-metadata';
import { singleton } from 'tsyringe';
import { DATA_SYNC_MODE, TASK_TYPE_ENUM } from '@/constant';
import { Modal } from 'antd';
import {
	ComponentVersion,
	CreateModel,
	ExeArgs,
	MainClass,
	Resource,
	SyncModel,
} from '@/components/createFormSlot';
import api from '@/api';
import type { FormInstance } from 'antd';

interface ITaskRenderService {
	renderCreateForm: (key: TASK_TYPE_ENUM) => JSX.Element;
}

@singleton()
class TaskRenderService implements ITaskRenderService {
	public renderCreateForm = (
		key: TASK_TYPE_ENUM,
		record?: any,
		form?: Omit<FormInstance, 'scrollToField' | '__INTERNAL__' | 'getFieldInstance'>,
	) => {
		switch (key) {
			case TASK_TYPE_ENUM.SYNC:
				return (
					<>
						<CreateModel disabled={!!record} />
						<SyncModel
							validator={async (_, value) => {
								if (record && value === DATA_SYNC_MODE.INCREMENT) {
									// 当编辑同步任务，且改变同步模式为增量模式时，需要检测任务是否满足增量同步的条件
									const res = await api.checkSyncMode({ id: record.id });
									if (res.code === 1) {
										return res.data ? Promise.resolve() : Promise.reject();
									}

									return Promise.reject(
										new Error('当前同步任务不支持增量模式！'),
									);
								}
								return Promise.resolve();
							}}
						/>
					</>
				);

			case TASK_TYPE_ENUM.SQL:
				return (
					<>
						<ComponentVersion
							onChange={() => {
								Modal.confirm({
									title: '正在切换引擎版本',
									content: (
										<>
											<span style={{ color: 'red' }}>
												切换引擎版本后将重置环境参数
											</span>
											，请确认是否继续？
										</>
									),
									onCancel: () => {
										form?.resetFields(['componentVersion']);
									},
								});
							}}
						/>
					</>
				);
			case TASK_TYPE_ENUM.FLINK:
				return (
					<>
						<Resource />
						<MainClass />
						<ExeArgs />
						<ComponentVersion />
					</>
				);
			case TASK_TYPE_ENUM.DATA_ACQUISITION:
				return (
					<>
						<CreateModel disabled={!!record} />
						<ComponentVersion
							onChange={() => {
								Modal.confirm({
									title: '正在切换引擎版本',
									content: (
										<>
											<span style={{ color: 'red' }}>
												切换引擎版本后将重置环境参数
											</span>
											，请确认是否继续？
										</>
									),
									onCancel: () => {
										form?.resetFields(['componentVersion']);
									},
								});
							}}
						/>
					</>
				);
			default:
				return <></>;
		}
	};
}

export default new TaskRenderService();
