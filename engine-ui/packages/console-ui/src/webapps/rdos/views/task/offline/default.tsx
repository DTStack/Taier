import * as React from 'react';
import { connect } from 'react-redux';
import { Row, Col } from 'antd';
import { isEmpty } from 'lodash';
import utils from 'utils';

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

import Workbench from './workbench';
import WelcomePage from './welcome';
import { isProjectCouldEdit } from '../../../comm';

class Default extends React.Component<any, any> {
    componentDidMount () {
        const taskId = utils.getParameterByName('taskId');
        if (taskId) {
            this.props.openTaskInDev(+taskId);
        }
    }
    render () {
        const {
            workbench,
            scriptTree,
            user,
            editor,
            toggleCreateTask,
            toggleUpload,
            toggleCreateScript,
            project
        } = this.props;
        const couldEdit = isProjectCouldEdit(project, user);
        const themeDark = editor.options.theme !== 'vs' ? true : undefined;
        const iconBaseUrl = themeDark ? '/public/rdos/img/theme-dark' : '/public/rdos/img';
        const { tabs, showPanel } = workbench;
        return (tabs.length && showPanel) ? (
            <Workbench />
        ) : (
            couldEdit && (
                <Row
                    className="box-card txt-left"
                    style={{ paddingTop: '30px' }}
                >
                    <Col className="operation-card">
                        <div
                            onClick={toggleCreateTask}
                            className="operation-content"
                        >
                            <img
                                src={`${iconBaseUrl}/create_task.png`}
                                className="anticon"
                            />
                            <p className="txt-center operation-title">
                                创建任务
                            </p>
                        </div>
                    </Col>
                    <Col className="operation-card">
                        <div
                            onClick={toggleUpload}
                            className="operation-content"
                        >
                            <img
                                src={`${iconBaseUrl}/upload_res.png`}
                                className="anticon"
                            />
                            <p className="txt-center operation-title">
                                上传资源
                            </p>
                        </div>
                    </Col>
                    {!isEmpty(scriptTree) && (
                        <Col className="operation-card">
                            <div
                                onClick={toggleCreateScript}
                                className="operation-content"
                            >
                                <img
                                    src={`${iconBaseUrl}/create_script.png`}
                                    className="anticon"
                                />
                                <p className="txt-center operation-title">
                                    创建脚本
                                </p>
                            </div>
                        </Col>
                    )}
                </Row>
            ) && (<WelcomePage />)
        );
    }
}

export default connect(
    (state: any) => {
        const { createTask, upload } = state.offlineTask.modalShow;
        const { workbench, scriptTree } = state.offlineTask;
        return {
            createTask,
            upload,
            workbench,
            scriptTree,
            project: state.project,
            user: state.user,
            editor: state.editor
        };
    },
    workbenchActions
)(Default);
