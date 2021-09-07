import molecule from "molecule";
import { Float, IStatusBarItem } from "molecule/esm/model";
import { TASK_TYPE } from "../../../comm/const";

export const STATUS_BAR_SPARK_SQL = {
    id: 'sparksql',
    sortIndex: 3,
    name: 'SparkSQL',
}
export const STATUS_BAR_SYNC = {
    id: 'sync',
    sortIndex: 3,
    name: 'DataSync',
}

export function getStatusBarLanguage(language: string) {
    switch(Number(language)) {
        case TASK_TYPE.SQL: {
            return STATUS_BAR_SPARK_SQL;
        }
        case TASK_TYPE.SYNC: {
            return STATUS_BAR_SYNC;
        }
        default: {
            return null;
        }
    }
}

export function updateStatusBarLanguage(item: IStatusBarItem | null) {
    if (!item) return;
    const languageStatus = molecule.statusBar.getStatusBarItem('language', Float.right);
    if (languageStatus) {
        molecule.statusBar.update(item);
    } else {
        molecule.statusBar.add(item, Float.right);
    }
}