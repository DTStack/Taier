import { Component } from '@dtinsight/molecule/esm/react';

export interface ITaskResultService {
	/**
	 * 增量添加日志信息
	 *
	 * 自动添加换行标识符
	 */
	appendLogs: (key: string, log: string) => void;
	/**
	 * 清除任务执行的日志信息
	 */
	clearLogs: (key: string) => void;
	/**
	 * 任务执行结果
	 */
	setResult: (key: string, result: any) => void;
	/**
	 * 清除执行结果
	 */
	clearResult: (key: string) => void;
}

export interface ITaskResultStates {
	/**
	 * 存储不同任务执行的日志结果
	 */
	logs: Record<string, string>;
	/**
	 * 任务执行结果
	 */
	results: Record<string, any>;
}

class TaskResultService extends Component<ITaskResultStates> implements ITaskResultService {
	protected state: ITaskResultStates;

	constructor() {
		super();
		this.state = {
			logs: {},
			// 后续 results 下载需要 jobId 下载
			results: {},
		};
	}

	public setResult(key: string, res: any) {
		const nextResults = this.state.results;
		nextResults[key] = res;
		this.setState({ results: nextResults });
	}

	public clearResult(key: string) {
		const nextResults = this.state.results;
		Reflect.deleteProperty(nextResults, key);
		this.setState({ results: nextResults });
	}

	public appendLogs(key: string, log: string) {
		const nextLogs = this.state.logs;
		nextLogs[key] = nextLogs[key] || '';
		nextLogs[key] += `${log}\n`;
		this.setState({
			logs: nextLogs,
		});
	}

	public clearLogs(key: string) {
		const nextLogs = this.state.logs;
		nextLogs[key] = '';
		this.setState({
			logs: nextLogs,
		});
	}
}

/**
 * 处理任务执行结果的 service
 */
export default new TaskResultService();
