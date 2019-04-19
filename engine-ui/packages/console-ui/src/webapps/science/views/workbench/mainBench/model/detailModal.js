import React from 'react';
import moment from 'moment';

import TaskParamsModal from '../../../../components/taskParamsModal';

class ModelDetailModal extends React.Component {
    render () {
        const { data } = this.props;
        return (
            <TaskParamsModal
                {...this.props}
                title='模型属性'
                data={data && [{
                    label: '实验名称',
                    value: data.name
                }, {
                    label: '模型来源',
                    value: data.taskDesc
                }, {
                    label: '调用API',
                    value: data.url
                }, {
                    label: '特征列',
                    value: data.tzl
                }, {
                    label: '目标列',
                    value: data.targetColumn
                }, {
                    label: '参数',
                    value: data.params
                }, {
                    label: '部署人',
                    value: data.deployName
                }, {
                    label: '部署时间',
                    value: moment(data.gmtModified).format('YYYY-MM-DD HH:mm:ss')
                }, {
                    label: '更新时间',
                    value: moment(data.gmtModified).format('YYYY-MM-DD HH:mm:ss')
                }]}
            />
        )
    }
}
export default ModelDetailModal;
