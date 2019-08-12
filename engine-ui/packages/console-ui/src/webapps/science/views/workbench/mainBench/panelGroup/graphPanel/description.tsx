import * as React from 'react';
import { connect } from 'react-redux';
import { COMPONENT_TYPE } from '../../../../../consts'
@(connect((state: any) => {
    return {
        selectedCell: state.component.selectedCell
    }
}) as any)
class Description extends React.Component<any, any> {
    shouldComponentUpdate (nextProps: any, nextState: any) {
        const selectedCell = nextProps.selectedCell;
        if (!this.isEmptyObejct(selectedCell) && selectedCell.mxObjectId !== this.props.selectedCell.mxObjectId) {
            return true
        }
        return false;
    }
    isEmptyObejct = (obejct: any) => {
        return Object.keys(obejct).length === 0
    }
    initTitle = () => {
        const { selectedCell } = this.props;
        let title = '';
        if (this.isEmptyObejct(selectedCell)) return '';
        switch (selectedCell.data.componentType) {
            case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE:
                title = '读数据表';
                break;
            case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE:
                title = '写数据表';
                break;
            case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT:
                title = 'SQL脚本';
                break;
            case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE:
                title = '类型转化';
                break;
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
                title = '归一化';
                break;
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT:
                title = '拆分';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION:
                title = '逻辑回归二分类';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION:
                title = 'GBDT回归';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION:
                title = 'kmeans聚类';
                break;
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT:
                title = '预测';
                break;
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
                title = '二分类评估';
                break;
            case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION:
                title = '回归模型评估';
                break;
            case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION:
                title = '聚类模型评估';
                break;
            default:
                return ''
        }
        return title;
    }
    initRender = () => {
        const { selectedCell } = this.props;
        let description = '';
        if (this.isEmptyObejct(selectedCell)) return '';
        switch (selectedCell.data.componentType) {
            case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE:
                description = '读取数据表组件，数据科学平台的数据存储在HDFS中，在参数配置中输入表名即可，暂不支持跨项目读表。';
                break;
            case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE:
                description = '写数据表组件，数据科学平台的数据存储在HDFS中，写数据表中可写在该项目下的已有表中，或新建表。若要写入分区表，需提前建好分区表，再进行写入。';
                break;
            case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT:
                description = 'SQL脚本组件，可自定义组件SQL进行数据处理。\n但SQL脚本的最后一条语句需为Select语句，作为此组件的输出。';
                break;
            case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE:
                description = '类型转化组件，对源表字段类型可进行转化，支持转化成double、int、string类型。且在转化值异常时，可进行默认值填充。';
                break;
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
                description = '归一化组件，对表的某列或多列进行归一化处理，产生的线数据存入新表中。目前支持线性函数转化，计算表达式为y=(x-min)/（max-min）。';
                break;
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT:
                description = '拆分组件，将原有数据按照比例进行随机拆分，最终输出两张表，主要用于生成训练集或测试集。';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION:
                description = '逻辑回归二分类组件，用于预测当前被观察的对象属于哪个组，最终提供离散的二进制（0或1）输出结果。';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION:
                description = 'GBDT回归组件，是一种迭代的决策树算法，将多颗决策树的结果累加起来作为最终的预测输出。进行回归预测。';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION:
                description = 'Kmeans聚类组件，以样本间距离为基础，将n个对象分为k个簇，使群体与群体之间的距离尽量大，而簇内具有较高的相似度。';
                break;
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT:
                description = '预测组件，用于模型预测。拥有2个输入，训练模型和预测数据，输出为预测结果。';
                break;
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
                description = '一个综合评估组件，里面包含综合指数、KS/PR/LIFT/ROC曲线及详细信息。';
                break;
            case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION:
                description = '回归指标：基于预测结果和原始结果，评价回归算法模型的优劣，指标包括MSE、MAE、MAPE、R平方值等。';
                break;
            case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION:
                description = '聚类模型评估组件，是评估原始数据进行聚类的可行性和聚类方法产生的结果的质量，主要包括：估计聚类趋势、确定数据集中的簇数、测定聚类质量。';
                break;
            default:
                return ''
        }
        const defaultDom = (
            <div className="panel-description">{description}</div>
        )
        return defaultDom;
    }
    render () {
        return (
            <div>
                <header className='c-panel__siderbar__header'>
                    {this.initTitle()}
                </header>
                {this.initRender()}
            </div>
        );
    }
}

export default Description;
