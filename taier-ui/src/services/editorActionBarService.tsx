import molecule from '@dtinsight/molecule';
import { Component } from '@dtinsight/molecule/esm/react';
import { cloneDeep } from 'lodash';
import { container, singleton } from 'tsyringe';

import editorActions from '@/components/scaffolds/editorActions';
import { ID_COLLECTIONS } from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import type { IExecuteService } from './executeService';
import ExecuteService from './executeService';
import { taskRenderService } from '.';

const { RUNNING_TASK, RUN_TASK, STOP_TASK } = editorActions;

interface IEditorActionBarState {
    runningTab: Set<number>;
}

interface IEditorActionBarService {
    /**
     * 针对不同的任务类型，渲染不同的 `toolbar`
     * @notice 需要确保 `current` 是数据是正确的
     */
    performSyncTaskActions: () => void;
}

@singleton()
export default class EditorActionBarService
    extends Component<IEditorActionBarState>
    implements IEditorActionBarService
{
    protected state: IEditorActionBarState;
    private executeService: IExecuteService;
    constructor() {
        super();
        this.state = {
            runningTab: new Set(),
        };

        this.executeService = container.resolve(ExecuteService);

        this.executeService.onStartRun((currentTabId) => {
            const { current } = molecule.editor.getState();
            this.setState({
                runningTab: this.state.runningTab.add(currentTabId),
            });
            if (current?.activeTab === currentTabId.toString()) {
                molecule.editor.updateActions([
                    RUNNING_TASK,
                    {
                        id: ID_COLLECTIONS.TASK_STOP_ID,
                        disabled: false,
                    },
                ]);
            }
        });

        this.executeService.onEndRun((currentTabId) => {
            const { current } = molecule.editor.getState();
            this.state.runningTab.delete(currentTabId);
            this.setState({
                runningTab: this.state.runningTab,
            });
            if (current?.activeTab === currentTabId.toString()) {
                molecule.editor.updateActions([RUN_TASK, STOP_TASK]);
            }
        });

        this.executeService.onStopTab((currentTabId) => {
            const { current } = molecule.editor.getState();
            this.state.runningTab.delete(currentTabId);
            this.setState({
                runningTab: this.state.runningTab,
            });
            if (current?.activeTab === currentTabId.toString()) {
                molecule.editor.updateActions([RUN_TASK, STOP_TASK]);
            }
        });
    }

    public performSyncTaskActions = () => {
        const { current } = molecule.editor.getState();
        if (current?.tab?.data) {
            const currentTabData: CatalogueDataProps & IOfflineTaskProps = current?.tab?.data;
            const taskToolbar = cloneDeep(
                taskRenderService.renderEditorActions(currentTabData.taskType, currentTabData)
            );

            molecule.editor.updateGroup(current.id, {
                actions: [...taskToolbar, ...molecule.editor.getDefaultActions()],
            });
            if (this.state.runningTab.has(currentTabData.id)) {
                molecule.editor.updateActions([
                    RUNNING_TASK,
                    {
                        id: ID_COLLECTIONS.TASK_STOP_ID,
                        disabled: false,
                    },
                ]);
            }
        } else if (current) {
            molecule.editor.updateGroup(current.id, {
                actions: [...molecule.editor.getDefaultActions()],
            });
        }
    };
}
