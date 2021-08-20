import React from 'react';
import molecule from 'molecule';
import type { IEditorTab, IExtension, IPanelItem } from 'molecule/esm/model';
import { localize } from 'molecule/esm/i18n/localize';
import { connect } from 'molecule/esm/react';
import EnvParams from '../../task/envParams';
import { SchedulingConfig } from '../../task/schedulingConfig';
import TaskParams from '../../task/taskParams';

import {
    ENV_PARAMS,
    TASK_PARAMS_ID,
    TASK_SCHEDULE_CONFIG,
} from '../utils/const';

function getEnvParamsUI() {
    const EnvParamsView = connect(molecule.editor, EnvParams);

    const handleValueChanged = (currentTab: IEditorTab, value: string) => {
        console.group('handleValueChanged');
        console.log('currentTab:', currentTab);
        console.log('value:', value);
        console.groupEnd();
    };

    return {
        id: ENV_PARAMS,
        name: '环境参数',
        sortIndex: -1,
        renderPane: () => <EnvParamsView onChange={handleValueChanged} />,
    };
}

function getTaskParamsUI() {
    const TaskParamsView = connect(molecule.editor, TaskParams);

    return {
        id: TASK_PARAMS_ID,
        name: localize(TASK_PARAMS_ID, '任务参数'),
        sortIndex: -2,
        config: {
            grow: 2,
        },
        renderPane: () => (
            <TaskParamsView
                tabData={{
                    taskVariables: [
                        {
                            paramName: '$system',
                            paramCommand: 'xiuneng',
                            type: 0,
                        },
                        {
                            paramName: '$key',
                            paramCommand: '',
                            type: 1,
                        },
                    ],
                }}
            />
        ),
    };
}

function getSchedulingConfigUI() {
    const SchedulingConfigView = connect(molecule.editor, SchedulingConfig);

    return {
        id: TASK_SCHEDULE_CONFIG,
        name: localize(TASK_SCHEDULE_CONFIG, '调度依赖'),
        sortIndex: -3,
        renderPane: () => (
            <SchedulingConfigView
                isPro={false}
                couldEdit={false}
                isScienceTask={false}
                tabData={{
                    scheduleConf: '{}',
                    scheduleStatus: 0,
                }}
                updateKey={0}
                key={'schedule-1'}
                isIncrementMode={false}
            />
        ),
    };
}

export default class PanelExtension implements IExtension {
    activate() {
        const panelItems: IPanelItem[] = [
            getSchedulingConfigUI(), 
            getTaskParamsUI(),
            getEnvParamsUI()
        ];
        molecule.panel.add(panelItems);
    }
}
