import molecule from 'molecule';
import { IExtension } from 'molecule/esm/model';

export default class ThemeExtension implements IExtension {
    activate() {
        // 初始化资源管理
        molecule.colorTheme.setTheme('Default Light+');
    }
}
