import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Icon } from 'antd';

import utils from 'utils';

import { HELP_DOC_URL, PROJECT_TYPE } from '../../../../../comm/const';
import { isProjectCouldEdit } from '../../../../../comm'
import CommonEditor from '../../commonEditor'
import Toolbar from './toolbar.js';

import { workbenchAction } from '../../../../../store/modules/offlineTask/actionType';

@connect(state => {
    return {
        project: state.project,
        user: state.user
    }
}, dispatch => {
    return {
        dispatch: dispatch,
        updateTaskFields (params) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    }
})
class DataSyncScript extends Component {
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
                    enableRun: false,
                    enableFormat: couldEdit,
                    disAbleEdit: !couldEdit,
                    onFormat: this.onFormat.bind(this),
                    rightCustomButton: this.getRightButton(),
                    leftCustomButton: couldEdit && this.getLeftButton()
                }}
            />
        );
    }
}

export default DataSyncScript;
