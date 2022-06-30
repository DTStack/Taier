import {
	CATELOGUE_TYPE,
	CREATE_MODEL_TYPE,
	DATA_SYNC_MODE,
	FLINK_VERSIONS,
	FLINK_VERSION_TYPE,
} from '@/constant';
import { Form, Input, Radio, Select } from 'antd';
import { syncModeHelp, syncTaskHelp } from '../helpDoc/docs';
import type { RuleObject } from 'antd/lib/form';
import type { StoreValue } from 'antd/lib/form/interface';
import FolderPicker from '../folderPicker';
import resourceManagerTree from '@/services/resourceManagerService';

/**
 * 配置模式物料
 */
export const CreateModel = ({ disabled }: { disabled: boolean }) => (
	<Form.Item
		label="配置模式"
		name="createModel"
		tooltip={syncTaskHelp}
		rules={[
			{
				required: true,
				message: '请选择配置模式',
			},
		]}
		initialValue={CREATE_MODEL_TYPE.GUIDE}
	>
		<Radio.Group disabled={disabled}>
			<Radio value={CREATE_MODEL_TYPE.GUIDE}>向导模式</Radio>
			<Radio value={CREATE_MODEL_TYPE.SCRIPT}>脚本模式</Radio>
		</Radio.Group>
	</Form.Item>
);

/**
 * 同步模式物料
 */
export const SyncModel = ({
	validator,
}: {
	validator?: (rule: RuleObject, value: StoreValue) => Promise<void | any> | void;
}) => (
	<Form.Item
		label="同步模式"
		name="syncModel"
		tooltip={syncModeHelp}
		rules={[
			{
				required: true,
				message: '请选择配置模式',
			},
			{
				validator,
			},
		]}
		initialValue={DATA_SYNC_MODE.NORMAL}
	>
		<Radio.Group>
			<Radio value={DATA_SYNC_MODE.NORMAL}>无增量标识</Radio>
			<Radio value={DATA_SYNC_MODE.INCREMENT}>有增量标识</Radio>
		</Radio.Group>
	</Form.Item>
);

/**
 * 引擎版本物料
 */
export const ComponentVersion = ({ onChange }: { onChange?: (value: string) => void }) => (
	<Form.Item label="引擎版本" name="componentVersion" initialValue={FLINK_VERSIONS.FLINK_1_12}>
		<Select onChange={onChange}>
			{FLINK_VERSION_TYPE.map(({ value, label }) => (
				<Select.Option key={value} value={value}>
					{label}
				</Select.Option>
			))}
		</Select>
	</Form.Item>
);

/**
 * 资源下拉菜单物料
 */
export const Resource = () => (
	<Form.Item
		label="资源"
		name="resourceIdList"
		rules={[
			{
				required: true,
				message: '请选择关联资源',
			},
			{
				validator: (_, value) => {
					const resouceTreeData =
						resourceManagerTree.getState().folderTree?.data?.[0]?.data;
					if (!resouceTreeData) return Promise.resolve();
					let nodeType: any;

					const loop = (arr: any) => {
						arr.forEach((node: any) => {
							if (node.id === value) {
								nodeType = node.type;
							} else {
								loop(node.children || []);
							}
						});
					};

					loop([resouceTreeData]);

					if (nodeType === 'folder') {
						return Promise.reject(new Error('请选择具体文件, 而非文件夹'));
					}

					return Promise.resolve();
				},
			},
		]}
	>
		<FolderPicker dataType={CATELOGUE_TYPE.RESOURCE} showFile />
	</Form.Item>
);

/**
 * mainClass 物料
 */
export const MainClass = () => (
	<Form.Item
		label="mainClass"
		name="mainClass"
		rules={[
			{
				required: true,
				message: '请选择 mainClass',
			},
		]}
	>
		<Input placeholder="请输入 mainClass" />
	</Form.Item>
);

/**
 * 命令行参数物料
 */
export const ExeArgs = () => (
	<Form.Item label="命令行参数" name="exeArgs">
		<Input placeholder="请输入命令行参数" />
	</Form.Item>
);
