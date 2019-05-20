import React from 'react';

import TaskParamsModal from '../../../../components/taskParamsModal';

class AlgorithmModal extends React.Component {
    render () {
        const { data = {} } = this.props;
        return (
            <TaskParamsModal
                {...this.props}
                title={data && data.name}
                data={data && [{
                    label: '特征列',
                    value: data.modelName
                }, {
                    label: '目标列',
                    value: data.origin
                }, {
                    label: '参数',
                    value: data.url
                }]}
            />
        )
    }
}
export default AlgorithmModal;
