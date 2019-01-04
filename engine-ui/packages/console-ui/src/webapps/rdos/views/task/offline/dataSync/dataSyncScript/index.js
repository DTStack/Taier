import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Icon, message } from 'antd';
import { bindActionCreators } from 'redux';

import utils from 'utils';

import { HELP_DOC_URL } from '../../../../../comm/const';
import { isProjectCouldEdit } from '../../../../../comm'
import CommonEditor from '../../commonEditor'
import Toolbar from './toolbar.js';

import {
    workbenchActions
} from '../../../../../store/modules/offlineTask/offlineAction';
import { workbenchAction } from '../../../../../store/modules/offlineTask/actionType';
import * as editorActions from '../../../../../store/modules/editor/editorAction';

@connect(state => {
    return {
        project: state.project,
        user: state.user
    }
}, dispatch => {
    const taskAc = workbenchActions(dispatch);
    const editorAc = bindActionCreators(editorActions, dispatch);
    const actions = Object.assign({
        dispatch: dispatch,
        updateTaskFields (params) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    }, editorAc, taskAc)
    return actions;
})
class DataSyncScript extends Component {
    componentDidMount () {
        const currentTab = this.props.id;
        if (currentTab) {
            this.props.getTab(currentTab)// 初始化console所需的数据结构
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    onFormat () {
        const { sqlText, dispatch } = this.props;
        const data = {
            merged: true
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
        const params = { taskId: currentTabData.id, name: currentTabData.name };
        execDataSync(id, params);
    }

    onStop = async () => {
        const { stopDataSync, id } = this.props;
        stopDataSync(id);
    }

    render () {
        const { taskCustomParams, id, sqlText, currentTabData, project, user } = this.props;
        const couldEdit = isProjectCouldEdit(project, user);
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
