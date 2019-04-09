import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import CommonEditor from '../../../../../components/commonEditor';

import reqUrls from '../../../../../consts/reqUrls';
import { siderBarType } from '../../../../../consts';

import workbenchActions from '../../../../../actions/workbenchActions';
import * as editorActions from '../../../../../actions/editorActions';
import commActions from '../../../../../actions';

@connect(
    state => {
        const { workbench, editor } = state;
        return {
            editor,
            workbench
        };
    },
    dispatch => {
        const actionsOne = bindActionCreators(workbenchActions, dispatch);
        const actionsTwo = bindActionCreators(editorActions, dispatch);
        const actionsThree = bindActionCreators(commActions, dispatch);
        return Object.assign(actionsOne, actionsTwo, actionsThree);
    }
)
class GraphPanel extends Component {
    removeConsoleTab = targetKey => {
        const { currentTab } = this.props;
        this.props.removeRes(currentTab, parseInt(targetKey, 10));
    };

    closeConsole = () => {
        const { currentTab } = this.props;
        this.props.resetConsole(currentTab, siderBarType.experiment);
    };

    render () {
        const { editor, data } = this.props;

        const currentTab = data.id;

        const consoleData = editor.console[siderBarType.experiment];
        const resultData = consoleData[currentTab] && consoleData[currentTab].data
            ? consoleData[currentTab].data
            : [];

        const toolbarOpts = {
            enable: true,
            enableRun: true,
            disableEdit: true,
            isRunning: editor.running.indexOf(currentTab) > -1,
            onRun: this.execConfirm,
            onStop: this.stopSQL
        };

        const consoleOpts = {
            data: resultData,
            onConsoleClose: this.closeConsole,
            onRemoveTab: this.removeConsoleTab,
            downloadUri: reqUrls.DOWNLOAD_SQL_RESULT
        };

        return (
            <CommonEditor
                console={consoleOpts}
                toolbar={toolbarOpts}
            >
                graphComponent
            </CommonEditor>
        );
    }
}

export default GraphPanel;
