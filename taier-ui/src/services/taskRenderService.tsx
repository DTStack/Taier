import 'reflect-metadata';
import { singleton } from 'tsyringe';
import { DATA_SYNC_MODE } from '@/constant';
import { Modal } from 'antd';
import scaffolds from '@/components/createFormSlot';
import api from '@/api';
import type { TASK_TYPE_ENUM } from '@/constant';
import type { FormInstance } from 'antd';

interface ITaskRenderService {
	createFormField: ICreateFormField[];
	/**
	 * 根据 key 值获取创建任务所需要的 UI 界面
	 */
	renderCreateForm: (key: TASK_TYPE_ENUM) => React.ReactNode;
}

interface ICreateFormField {
	taskType: TASK_TYPE_ENUM;
	formField: (keyof typeof scaffolds)[];
}

@singleton()
class TaskRenderService implements ITaskRenderService {
	public createFormField: ICreateFormField[] = [];

	constructor() {
		fetch('./layout/create.json', { method: 'GET' })
			.then<ICreateFormField[]>((res) => res.json())
			.then((res) => {
				this.createFormField = res;
			});
	}

	public renderCreateForm = (
		key: TASK_TYPE_ENUM,
		record?: Record<string, any>,
		form?: Omit<FormInstance, 'scrollToField' | '__INTERNAL__' | 'getFieldInstance'>,
	) => {
		const field = this.createFormField.find((i) => i.taskType === key);
		if (!field) {
			return null;
		}

		return (
			<>
				{field.formField.map((f) => {
					const Compoennt = scaffolds[f];
					return (
						<Compoennt
							key={f}
							disabled={!!record}
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
					);
				})}
			</>
		);
	};
}

export default new TaskRenderService();
