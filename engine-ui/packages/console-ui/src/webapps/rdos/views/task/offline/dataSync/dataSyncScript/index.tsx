import * as React from 'react';
import { connect } from 'react-redux';
import { Icon, message } from 'antd';
import { bindActionCreators } from 'redux';

import utils from 'utils';

import { HELP_DOC_URL } from '../../../../../comm/const';
import { isProjectCouldEdit } from '../../../../../comm'
import CommonEditor from '../../commonEditor'
import Toolbar from './toolbar';

import {
    workbenchActions
} from '../../../../../store/modules/offlineTask/offlineAction';
import { workbenchAction } from '../../../../../store/modules/offlineTask/actionType';
import * as editorActions from '../../../../../store/modules/editor/editorAction';

@(connect((state: any) => {
    return {
        project: state.project,
        user: state.user
    }
}, (dispatch: any) => {
    const taskAc = workbenchActions(dispatch);
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign({
        dispatch: dispatch,
        updateTaskFields (params: any) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    }, editorAc, taskAc)
    return actions;
}) as any)
class DataSyncScript extends React.Component<any, any> {
    componentDidMount () {
        const currentTab = this.props.id;
        if (currentTab) {
            this.props.getTab(currentTab)// 初始化console所需的数据结构
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    onFormat () {
        const { sqlText, dispatch } = this.props;
        const data: any = {
            merged: true
        }
        if (!sqlText) {
            return;
        }
        data.sqlText = utils.jsonFormat(sqlText);

        if (!data.sqlText) {
            message.error('您的JSON格式有误')
            return;
        }
        dispatch({
            type: workbenchAction.SET_TASK_SQL_FIELD_VALUE,
            payload: data
        })
    }
    getRightButton () {
        return (
            <span>
                <Icon
                    style={{ color: '#2491F7', marginRight: '2px' }}
                    type="question-circle-o" />
                <a
                    href={HELP_DOC_URL.DATA_SYNC}
                    target="blank">
                    帮助文档
                </a>
            </span>
        )
    }
    getLeftButton () {
        return <Toolbar {...this.props} />
    }

    onRun = async () => {
        const { execDataSync, currentTabData, id } = this.props;
        const params: any = { taskId: currentTabData.id, name: currentTabData.name };
        execDataSync(id, params);
    }

    onStop = async () => {
        const { stopDataSync, id } = this.props;
        stopDataSync(id);
    }

    render () {
        const { taskCustomParams, id, sqlText, currentTabData, project, user } = this.props;
        const couldEdit = isProjectCouldEdit(project, user);
        const isLocked = currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock;
        const unSave = currentTabData.notSynced; // 未保存的同步任务无法运行

        return (
            <CommonEditor
                mode="json"
                taskCustomParams={taskCustomParams}
                key={id}
                value={sqlText}
                currentTab={id}
                currentTabData={currentTabData}
                toolBarOptions={{
                    enableRun: true,
                    enableFormat: couldEdit,
                    disableEdit: !couldEdit,
                    disableRun: isLocked || unSave,
                    onRun: this.onRun,
                    onStop: this.onStop,
                    onFormat: this.onFormat.bind(this),
                    rightCustomButton: this.getRightButton(),
                    leftCustomButton: couldEdit && this.getLeftButton()
                }}
            />
        );
    }
}

export default DataSyncScript;
