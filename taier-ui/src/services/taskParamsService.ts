import api from '@/api';
import { PARAMS_ENUM } from '@/constant';
import { Component } from '@dtinsight/molecule/esm/react';

interface IParamsProps {
	id?: number;
	paramCommand: string;
	paramName: string;
	type: PARAMS_ENUM;
}

interface ITaskParamsService {
	/**
	 * 根据 sql 匹配系统参数和自定义参数
	 */
	matchTaskParams: (sql: string) => Partial<IParamsProps>[];
}

interface ITaskParamsStates {
	systemParams: IParamsProps[];
}

class TaskParamsService extends Component<ITaskParamsStates> implements ITaskParamsService {
	protected state: ITaskParamsStates;
	constructor() {
		super();
		this.state = {
			systemParams: [],
		};
		this.getSystemParams();
	}

	private getSystemParams = () => {
		api.getCustomParams().then((res) => {
			if (res.code === 1) {
				this.setState({
					systemParams: res.data ?? [],
				});
			}
		});
	};

	public matchTaskParams = (sqlText: string) => {
		const regx = /\$\{([.\w]+)\}/g;
		const data: Partial<IParamsProps>[] = [];
		let res = null;
		// eslint-disable-next-line no-cond-assign
		while ((res = regx.exec(sqlText)) !== null) {
			const name = res[1];
			const param: Partial<IParamsProps> = {
				paramName: name,
				paramCommand: '',
			};
			const sysParam = this.state.systemParams.find((item) => item.paramName === name);
			if (sysParam) {
				param.type = PARAMS_ENUM.SYSTEM;
				param.paramCommand = sysParam.paramCommand;
			} else {
				param.type = PARAMS_ENUM.CUSTOM;
			}
			// 去重
			const exist = data.find((item) => name === item.paramName);
			if (!exist) {
				data.push(param);
			}
		}
		return data;
	};
}

export default new TaskParamsService();
