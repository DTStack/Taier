import React, { Component } from 'react';

import { TASK_TYPE, LEARNING_TYPE, PYTON_VERSION, SCRIPT_TYPE } from '../../comm/const';

export default class TabIcon extends Component {
    render () {
        const { tabData } = this.props;
        const { pythonVersion, learningType, scriptText, taskType, type } = tabData;

        let iconName = '';
        if (scriptText == null) {
            switch (taskType) {
                case TASK_TYPE.CUBE_KYLIN: {
                    iconName = 'cube_kylin.svg';
                    break;
                }
                case TASK_TYPE.SQL: {
                    iconName = 'sparksql.svg';
                    break;
                }
                case TASK_TYPE.MR:
                case TASK_TYPE.HAHDOOPMR: {
                    iconName = 'mr.svg';
                    break;
                }
                case TASK_TYPE.SYNC: {
                    iconName = 'datasync.svg';
                    break;
                }
                case TASK_TYPE.PYTHON: {
                    iconName = 'spark_python.svg';
                    break;
                }
                case TASK_TYPE.VIRTUAL_NODE: {
                    iconName = 'virtual.svg';
                    break;
                }
                case TASK_TYPE.DEEP_LEARNING: {
                    if (learningType == LEARNING_TYPE.MXNET) {
                        iconName = 'mxnet.svg';
                        break;
                    } else if (learningType == LEARNING_TYPE.TENSORFLOW) {
                        iconName = 'tensorflow.svg';
                        break;
                    } else {
                        iconName = 'deepLearning.svg';
                        break;
                    }
                }
                case TASK_TYPE.PYTHON_23: {
                    if (pythonVersion == PYTON_VERSION.PYTHON2) {
                        iconName = 'python2.svg';
                        break;
                    } else if (pythonVersion == PYTON_VERSION.PYTHON3) {
                        iconName = 'python3.svg';
                        break;
                    } else {
                        iconName = 'python.svg';
                        break;
                    }
                }
                case TASK_TYPE.SHELL: {
                    iconName = 'shell.svg';
                    break;
                }
                case TASK_TYPE.ML: {
                    iconName = 'computerStudy.svg';
                    break;
                }
                case TASK_TYPE.DATA_COLLECTION: {
                    iconName = 'collection.svg';
                    break;
                }
                case TASK_TYPE.WORKFLOW: {
                    iconName = 'workflow.svg';
                    break;
                }
                case TASK_TYPE.CARBONSQL: {
                    iconName = 'carbonsql.svg';
                    break;
                }
                case TASK_TYPE.NOTEBOOK: {
                    iconName = 'notebook.svg';
                    break;
                }
                case TASK_TYPE.EXPERIMENT: {
                    iconName = 'experiment.svg';
                    break;
                }
                case TASK_TYPE.LIBRASQL: {
                    iconName = 'librasql.svg';
                    break;
                }
                default:
                    return '';
            }
        } else {
            switch (type) {
                case SCRIPT_TYPE.SQL: {
                    iconName = 'sparksql.svg';
                    break;
                }
                case SCRIPT_TYPE.PYTHON2: {
                    iconName = 'python2.svg';
                    break;
                }
                case SCRIPT_TYPE.PYTHON3: {
                    iconName = 'python3.svg';
                    break;
                }
                case SCRIPT_TYPE.SHELL: {
                    iconName = 'shell.svg';
                    break;
                }
                case SCRIPT_TYPE.LIBRASQL: {
                    iconName = 'librasql.svg';
                    break;
                }
                default:
                    return '';
            }
        }

        return <img className="s-icon"
            style={{
                width: '14px',
                height: '14px',
                position: 'absolute',
                top: '8px',
                left: '10px'
            }}
            src={`/public/rdos/img/${iconName}`} />;
    }
}
