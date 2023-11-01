import { GlobalEvent } from '@dtinsight/molecule/esm/common/event';
import { singleton } from 'tsyringe';
import 'reflect-metadata';

enum ViewStorageEventKind {
    onStorageChange = 'onStorageChange',
}

// 负责整个应用的视图的持久化
@singleton()
class ViewStoreService extends GlobalEvent {
    private viewStorage = new Map<string, any>();

    public setViewStorage = <T>(tabId: string, value: ((preVal: T) => T) | T) => {
        if (typeof value === 'function') {
            this.viewStorage.set(tabId, (value as (preVal: T) => T)(this.getViewStorage(tabId)));
        } else {
            this.viewStorage.set(tabId, value);
        }
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

    public emiStorageChange(tabId: string) {
        this.emit(ViewStorageEventKind.onStorageChange, tabId);
    }

    public onStorageChange(callback: (tabId: string) => void) {
        this.subscribe(ViewStorageEventKind.onStorageChange, callback);
    }
}

export default new ViewStoreService();
