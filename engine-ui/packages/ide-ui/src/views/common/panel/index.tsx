import React from 'react';
import molecule from 'molecule';
import {
    IEditorTab,
    IExtension,
    PANEL_OUTPUT,
    IPanelItem,
} from 'molecule/esm/model';
import { localize } from 'molecule/esm/i18n/localize';
import { connect } from 'molecule/esm/react';
import EnvParams from '../../task/envParams';
import { SchedulingConfig } from '../../task/schedulingConfig';
import TaskParams from '../../task/taskParams';
import Markdown from '../markdown';
import {
    ENV_PARAMS,
    TASK_PARAMS_ID,
    TASK_SCHEDULE_CONFIG,
    OUTPUT_LOG,
} from '../utils/const';

function getEnvParamsUI() {
    const EnvParamsView = connect(molecule.editor, EnvParams);

    const handleValueChanged = (currentTab: IEditorTab, value: string) => {
        const tab = {
            ...currentTab,
            data: {
                ...currentTab.data,
                taskParams: value
            }
        }
        molecule.editor.updateTab(tab)
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
    const changeScheduleConf = (currentTab: IEditorTab, value: any) => {
        const { data } = currentTab
        const tab = {
            ...currentTab,
            data: {
                ...data,
                ...value
            }
        }
        molecule.editor.updateTab(tab)
    };
    return {
        id: TASK_SCHEDULE_CONFIG,
        name: localize(TASK_SCHEDULE_CONFIG, '调度依赖'),
        sortIndex: -3,
        renderPane: () => (
            <SchedulingConfigView
                changeScheduleConf={changeScheduleConf}
                key={'schedule-1'}
                isIncrementMode={false}
                isPro={false}
                couldEdit={false}
                isScienceTask={false}
                isWorkflowNode={false}
            />
        ),
    };
}

export default class PanelExtension implements IExtension {
    activate() {
        const panelItems: IPanelItem[] = [
            getSchedulingConfigUI(),
            getTaskParamsUI(),
            getEnvParamsUI(),
        ];
        molecule.panel.add(panelItems);

        molecule.panel.remove(PANEL_OUTPUT);
        molecule.panel.add({
            id: OUTPUT_LOG,
            name: '日志',
            renderPane: () => <Markdown />,
        });
        molecule.panel.setActive(OUTPUT_LOG);
    }
}
