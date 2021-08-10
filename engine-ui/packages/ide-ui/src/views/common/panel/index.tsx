import molecule from 'molecule'
import type { IEditorTab, IExtension } from 'molecule/esm/model'
import { connect } from 'molecule/esm/react'
import EnvParams from '../../task/envParams'
import { ENV_PARAMS } from '../utils/const'

function initEnvParams () {
    const EnvParamsView = connect(molecule.editor, EnvParams)

    const handleValueChanged = (currentTab: IEditorTab, value: string) => {
        console.group('handleValueChanged')
        console.log('currentTab:', currentTab)
        console.log('value:', value)
        console.groupEnd()
    }

    molecule.panel.add({
        id: ENV_PARAMS,
        name: '环境参数',
        renderPane: () => <EnvParamsView onChange={handleValueChanged} />
    })
}

export default class PanelExtension implements IExtension {
    activate () {
        initEnvParams()
    }
}
