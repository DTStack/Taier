import React from 'react';
import molecule from 'molecule';
import { IEditorTab, IExtension, PANEL_OUTPUT } from 'molecule/esm/model';
import { MarkdownRender } from 'dt-react-component';
import { connect } from 'molecule/esm/react';
import EnvParams from '../../task/envParams';
import { ENV_PARAMS, OUTPUT_LOG } from '../utils/const';

function initEnvParams() {
    const EnvParamsView = connect(molecule.editor, EnvParams);

    const handleValueChanged = (currentTab: IEditorTab, value: string) => {
        console.group('handleValueChanged');
        console.log('currentTab:', currentTab);
        console.log('value:', value);
        console.groupEnd();
    };

    molecule.panel.add({
        id: ENV_PARAMS,
        name: '环境参数',
        renderPane: () => <EnvParamsView onChange={handleValueChanged} />,
    });
}

export default class PanelExtension implements IExtension {
    activate() {
        initEnvParams();

        molecule.panel.remove(PANEL_OUTPUT);
        molecule.panel.add({
            id: OUTPUT_LOG,
            name: '日志',
            sortIndex: 1,
            renderPane: () => (
                <div>
                    <MarkdownRender text="[test](https://www.baidu.com)" />
                </div>
            ),
        });
    }
}
