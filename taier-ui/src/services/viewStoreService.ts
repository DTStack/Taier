import 'reflect-metadata';
import { singleton } from 'tsyringe';

@singleton()
class ViewStoreService {
	private viewStorage = new Map<string, any>();

	public setViewStorage = <T>(tabId: string, value: T) => {
		this.viewStorage.set(tabId, value);
	};

	public getViewStorage = <T>(tabId: string) => {
		return <T>this.viewStorage.get(tabId);
	};

	/**
	 * 删除 map 中存储的值, 若存在参数则删除某一个值，不存在则清空全部
	 */
	public clearStorage = (tabId?: string) => {
		if (tabId === undefined) {
			this.viewStorage.clear();
			return true;
		}

		return this.viewStorage.delete(tabId);
	};
}

export default new ViewStoreService();
