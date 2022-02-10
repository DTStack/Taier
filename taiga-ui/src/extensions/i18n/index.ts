import { IContributeType, IExtension } from "@dtinsight/molecule/esm/model";

const zhCN = require('./zh-CN.json');
const locales = [zhCN];

export const LocaleExtension: IExtension = {
    id: 'LocaleExtension',
    name: 'Locale Extension',
    contributes: {
        [IContributeType.Languages]: locales,
    },
    activate() {},
    dispose() {},
};
