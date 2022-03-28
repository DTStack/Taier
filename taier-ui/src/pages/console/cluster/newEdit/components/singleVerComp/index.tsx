import { Form } from 'antd';
import FileConfig from '../../fileConfig';
import FormConfig from '../../formConfig';
import ToolBar from '../toolbar';
import { useContextForm } from '../../context';
import type {
	IComponentProps,
	IClusterInfo,
	ISaveCompsData,
	IVersionData,
    ISaveComp,
    IHandleConfirm,
    ITestConnects,
    IHandleCompVersion,
} from '../../interface';

interface IProps {
	comp: IComponentProps;
	view: boolean;
	saveCompsData: ISaveCompsData[];
	versionData: IVersionData;
	clusterInfo: IClusterInfo;
	disabledMeta?: boolean;
	isCheckBoxs?: boolean;
	isSchedulings?: boolean;
	isDefault?: boolean;
	isMulitple?: boolean;
	saveComp: ISaveComp;
	handleConfirm: IHandleConfirm;
	testConnects: ITestConnects;
	handleCompVersion?: IHandleCompVersion;
}

export default function SingleVerComp({
	comp,
	view,
	saveCompsData,
	versionData,
	clusterInfo,
	disabledMeta,
	isCheckBoxs,
	isSchedulings,
	isDefault,
	isMulitple,
	saveComp,
	handleConfirm,
	testConnects,
	handleCompVersion,
}: IProps) {
	const form = useContextForm();

	return (
		<Form
			form={form}
			preserve={false}
			className="dt-cluster-content"
			scrollToFirstError
			labelCol={{
				span: 24,
			}}
			wrapperCol={{
				span: 24,
			}}
		>
			<FileConfig
				comp={comp}
				view={view}
				disabledMeta={disabledMeta}
				isDefault={isDefault}
				isCheckBoxs={isCheckBoxs}
				isSchedulings={isSchedulings}
				versionData={versionData}
				saveCompsData={saveCompsData}
				clusterInfo={clusterInfo}
				saveComp={saveComp}
				handleCompVersion={handleCompVersion}
			/>
			<FormConfig comp={comp} view={view} />
			{!view && (
				<ToolBar
					comp={comp}
					clusterInfo={clusterInfo}
					mulitple={isMulitple}
					saveComp={saveComp}
					testConnects={testConnects}
					handleConfirm={handleConfirm}
				/>
			)}
		</Form>
	);
}
