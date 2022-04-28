import { useMemo, useState } from 'react';
import { Radio } from 'antd';
import type { ITaskParams } from '@/interface';
import Editor from '@/components/editor';
import { prettierJSONstring } from '@/utils';
import { DATA_SOURCE_ENUM, TASK_TYPE_ENUM, CREATE_MODEL_TYPE } from '@/constant';
import Address from './address';
import ResultTable from './resultTable';

interface IProps {
	data?: Partial<ITaskParams>;
}

enum TAB_KEYS {
	CODE = 'code',
	SOURCE = 'source',
	SINK = 'sink',
	SIDE = 'side',
	RESULT_TABLE = 'resultTable',
	ENV = 'env',
	ADDRESS = 'address',
}

export default function RunCode({ data }: IProps) {
	const [tabKey, setTabKey] = useState<TAB_KEYS>(TAB_KEYS.ENV);

	const renderContent = (key: TAB_KEYS) => {
		switch (key) {
			case TAB_KEYS.CODE:
				return (
					<Editor
						sync
						style={{ height: '100%' }}
						language={data?.taskType === TASK_TYPE_ENUM.SQL ? 'hivesql' : 'json'}
						options={{ readOnly: true, minimap: { enabled: false } }}
						value={prettierJSONstring(data?.sqlText || '')}
					/>
				);
			case TAB_KEYS.SOURCE:
				return (
					<Editor
						sync
						style={{ height: '100%' }}
						language="sql"
						options={{ readOnly: true, minimap: { enabled: false } }}
						value={data?.sourceParams}
					/>
				);
			case TAB_KEYS.SINK:
				return (
					<Editor
						sync
						style={{ height: '100%' }}
						language="sql"
						options={{ readOnly: true, minimap: { enabled: false } }}
						value={data?.sinkParams}
					/>
				);
			case TAB_KEYS.SIDE:
				return (
					<Editor
						sync
						style={{ height: '100%' }}
						language="sql"
						options={{ readOnly: true, minimap: { enabled: false } }}
						value={data?.sideParams}
					/>
				);
			case TAB_KEYS.RESULT_TABLE:
				return <ResultTable taskId={data?.id} />;
			case TAB_KEYS.ENV:
				return (
					<Editor
						sync
						style={{ height: '100%' }}
						language="ini"
						options={{ readOnly: true, minimap: { enabled: false } }}
						value={data?.taskParams}
					/>
				);
			case TAB_KEYS.ADDRESS:
				return <Address taskId={data?.id} />;
			default:
				return null;
		}
	};

	const isflinkSql = useMemo(() => data?.taskType === TASK_TYPE_ENUM.SQL, [data]);
	const isGuideMode = useMemo(() => data?.createModel !== CREATE_MODEL_TYPE.SCRIPT, [data]);
	const isShowAddress = useMemo(
		() =>
			data?.taskType == TASK_TYPE_ENUM.DATA_ACQUISITION &&
			data?.originSourceType == DATA_SOURCE_ENUM.BEATS,
		[data],
	);
	const isShowResultTable = useMemo(
		() =>
			data?.taskType == TASK_TYPE_ENUM.DATA_ACQUISITION &&
			data?.targetSourceType == DATA_SOURCE_ENUM.HIVE,
		[data],
	);

	return (
		<div className="m-tabs h-full">
			<Radio.Group
				style={{ padding: '12px 20px' }}
				value={tabKey}
				onChange={(e) => setTabKey(e.target.value)}
			>
				<Radio.Button value={TAB_KEYS.CODE}>运行代码</Radio.Button>
				{isflinkSql && isGuideMode && (
					<>
						{/* <Radio.Button value={TAB_KEYS.SOURCE}>源表</Radio.Button>
						<Radio.Button value={TAB_KEYS.SINK}>结果表</Radio.Button>
						<Radio.Button value={TAB_KEYS.SIDE}>维表</Radio.Button> */}
					</>
				)}
				{isShowResultTable && (
					<Radio.Button value={TAB_KEYS.RESULT_TABLE}>结果表</Radio.Button>
				)}
				<Radio.Button value={TAB_KEYS.ENV}>环境参数</Radio.Button>
				{isShowAddress && <Radio.Button value={TAB_KEYS.ADDRESS}>运行地址</Radio.Button>}
			</Radio.Group>
			{renderContent(tabKey)}
		</div>
	);
}
