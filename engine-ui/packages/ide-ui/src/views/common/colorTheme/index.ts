import molecule from '@dtinsight/molecule';
import { IExtension } from '@dtinsight/molecule/esm/model';

export default class ThemeExtension implements IExtension {
    activate() {
        // 初始化资源管理
        molecule.colorTheme.setTheme('Default Light+');
    }
}
