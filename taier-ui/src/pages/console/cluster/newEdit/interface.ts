import type {
	COMPONENT_TYPE_VALUE,
	ENGINE_SOURCE_TYPE_ENUM,
	FLINK_DEPLOY_NAME,
} from '@/constant';

export interface IComponentProps {
	componentConfig: string;
	componentName: string;
	componentTemplate: string;
	componentTypeCode: number;
	deployType: keyof typeof FLINK_DEPLOY_NAME;
	engineId: ENGINE_SOURCE_TYPE_ENUM;
	gmtCreate: number;
	gmtModified: number;
	versionName: string;
	id: number;
	isDefault: boolean;
	isMetadata?: number;
	storeType: number;
	uploadFileName: string;
	kerberosFileName: string;
	mergeKrb5Content: string;
	paramsFile?: string;
	principal?: string;
	principals?: string;
}

export interface IScheduleComponentComp {
	componentTypeCode: COMPONENT_TYPE_VALUE;
	multiVersion: (IComponentProps | undefined)[];
}

export interface IScheduleComponent {
	components: IScheduleComponentComp[];
	schedulingCode: number;
	schedulingName: string;
}

export interface ISaveCompsData {
	key: number;
	value: string;
}

export interface IModifyComp {
	typeCode: COMPONENT_TYPE_VALUE;
	versionName?: string;
}

export interface IEditClusterRefProps {
	testConnects: (params?: ITestConnectsParams, callBack?: (bool: boolean) => void) => void;
	handleComplete: () => void;
}

export type IConfirmComps = IScheduleComponentComp | IComponentProps | COMPONENT_TYPE_VALUE[];

export interface IGetLoadTemplateParams {
	compVersion?: string;
	storeType?: number;
	deployType?: number;
}

export interface ITestConnectsParams {
	versionName?: string;
	typeCode?: COMPONENT_TYPE_VALUE;
	deployType?: number;
}

export interface IClusterInfo {
	clusterId: string;
	clusterName: string;
}

export interface ITestErrorMsg {
	componentVersion?: string | null;
	errorMsg: string | null;
}

export interface ITestStatusProps {
	clusterResourceDescription?: null | string;
	componentTypeCode: COMPONENT_TYPE_VALUE;
	componentVersion?: string | null;
	multiVersion?: ITestStatusProps[];
	errorMsg: string | null | ITestErrorMsg[];
	result: null | boolean;
}

export interface IVersionProps {
	dependencyKey: string | null;
	dependencyValue: string | null;
	deployTypes: number[] | null;
	id: number;
	key: string;
	required: boolean;
	type: string | null;
	value: string;
	values: IVersionProps[] | null;
}

export type IVersionData = Record<string, IVersionProps[]>;

export interface ICompTemplate {
	dependencyKey?: string;
	dependencyValue?: string;
	key: string;
	required: boolean;
	type: string;
	value: string | string[];
	values?: ICompTemplate[];
	isSameKey?: boolean;
}

export type ISaveComp = (params: Partial<IComponentProps>, type?: string) => void;

export type IHandleConfirm = (action: string, comps: IConfirmComps, mulitple?: boolean) => void;

export type ITestConnects = (params?: ITestConnectsParams, callBack?: (bool: boolean) => void) => void;

export type IHandleCompVersion = (typeCode: number, version: string) => void;
