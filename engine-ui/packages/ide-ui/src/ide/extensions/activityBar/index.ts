import molecule from 'molecule'
import { IExtension } from 'molecule/esm/model'

export function init () {
    // molecule.activityBar.addBar([
    //     {
    //         id: '任务',
    //         title: '任务开发',
    //         name: '任务开发',
    //     }
    // ])

}

export class ExtendsAccBar implements IExtension {
    activate (extensionCtx: molecule.IExtensionService): void {
        init()
    }
}
