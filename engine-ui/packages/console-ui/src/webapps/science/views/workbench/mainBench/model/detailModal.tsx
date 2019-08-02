import * as React from 'react';
import moment from 'moment';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import TaskParamsModal from '../../../../components/taskParamsModal';
import * as notebookActions from '../../../../actions/notebookActions';
import * as experimentActions from '../../../../actions/experimentActions';
import workbenchActions from '../../../../actions/workbenchActions';
import { modelComponentType, siderBarType } from '../../../../consts';

@(connect(null, (dispatch: any) => {
    return {
        ...bindActionCreators(notebookActions, dispatch),
        ...bindActionCreators(experimentActions, dispatch),
        ...bindActionCreators(workbenchActions, dispatch)
    }
}) as any)
class ModelDetailModal extends React.Component<any, any> {
    async openNewTask (id: any, isNotebook: any) {
        if (isNotebook) {
            let res = await this.props.openNotebook(id);
            if (res) {
                this.props.changeSiderBar(siderBarType.notebook);
            }
        } else {
            let res = await this.props.openExperiment(id);
            if (res) {
                this.props.changeSiderBar(siderBarType.experiment);
            }
        }
    }
    render () {
        const { data } = this.props;
        return (
            <TaskParamsModal
                {...this.props}
                title='模型属性'
                data={data && [{
                    label: '模型名称',
                    value: data.modelName
                }, {
                    label: '模型来源',
                    value: (<a onClick={this.openNewTask.bind(this, data.algorithmId, data.componentType == modelComponentType.NOTEBOOK.value)}>{data.algorithmName}</a>)
                }, {
                    label: '调用API',
                    value: data.apiUrl
                }, {
                    label: '特征列',
                    value: data.col
                }, {
                    label: '目标列',
                    value: data.label
                }, {
                    label: '参数',
                    value: data.params
                }, {
                    label: '封装算法',
                    value: data.componentName
                }, {
                    label: '部署人',
                    value: data.createUserName
                }, {
                    label: '部署时间',
                    value: moment(data.gmtCreate).format('YYYY-MM-DD HH:mm:ss')
                }, {
                    label: '更新时间',
                    value: moment(data.gmtModified).format('YYYY-MM-DD HH:mm:ss')
                }]}
            />
        )
    }
}
export default ModelDetailModal;
