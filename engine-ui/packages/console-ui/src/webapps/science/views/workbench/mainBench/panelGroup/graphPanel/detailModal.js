import React from 'react';
import moment from 'moment';

import TaskParamsModal from '../../../../../components/taskParamsModal';

class ModelDetailModal extends React.Component {
    render () {
        const { data } = this.props;
        return (
            <TaskParamsModal
                {...this.props}
                title='模型描述'
                data={data && [{
                    label: '模型名称',
                    value: data.name
                }, {
                    label: '节点名称',
                    value: data.taskDesc
                }, {
                    label: '算法名称',
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
