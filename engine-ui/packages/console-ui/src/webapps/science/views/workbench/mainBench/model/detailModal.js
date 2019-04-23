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
                    label: '模型名称',
                    value: data.modelName
                }, {
                    label: '模型来源',
                    value: data.origin
                }, {
                    label: '调用API',
                    value: data.url
                }, {
                    label: '封装算法',
                    value: data.codeName && data.codeName.map((m) => {
                        return m.name
                    }).join(', ')
                }, {
                    label: '部署人',
                    value: data.deployName
                }, {
                    label: '部署时间',
                    value: moment(data.gmtModified).format('YYYY-MM-DD HH:mm:ss')
                }, {
                    label: '更新时间',
                    value: moment(data.updateDate).format('YYYY-MM-DD HH:mm:ss')
                }]}
            />
        )
    }
}
export default ModelDetailModal;
