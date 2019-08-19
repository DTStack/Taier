import * as React from 'react';
import moment from 'moment';
import { get } from 'lodash';
import TaskParamsModal from '../../../../../components/taskParamsModal';

class ModelDetailModal extends React.Component<any, any> {
    componentType = (componentType: any) => {
        switch (componentType) {
            case 1: return '读数据表';
            case 2: return '写数据表';
            case 3: return 'sql脚本';
            case 4: return '类型转换';
            case 5: return '归一化';
            case 6: return '拆分';
            case 7: return '逻辑二分类';
            case 8: return '数据预测';
            case 9: return '二分类评估';
            default: return '未知';
        }
    }
    getParams = (data: any) => {
        return (
            <div style={{ margin: '10px 0' }}>
                <p>{`正则项：${data.penalty.toUpperCase()}`}</p>
                <p>{`最大迭代次数：${data.max_iter}`}</p>
                <p>{`正则系数：${data.pos}`}</p>
                <p>{`最小收敛误差：${data.tol}`}</p>
            </div>
        )
    }
    render () {
        const data = get(this, 'props.data.data')
        return (
            <TaskParamsModal
                {...this.props}
                title='模型描述'
                data={data && [{
                    label: '节点名称',
                    value: data.name
                }, {
                    label: '算法名称',
                    value: this.componentType(data.componentType)
                }, {
                    label: '特征列',
                    value: data.logisticComponent.col.map((item: any) => item.key).join(',')
                }, {
                    label: '目标列',
                    value: data.logisticComponent.label.key
                }, {
                    label: '参数',
                    value: this.getParams(data.logisticComponent)
                }, {
                    label: '创建时间',
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
