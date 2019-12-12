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
            case COMPONENT_TYPE.DATA_MERGE.STANDARD:
                title = '标准化';
                break;
            case COMPONENT_TYPE.DATA_MERGE.MISS_VALUE:
                title = '缺失值填充';
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
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS:
                title = 'GBDT二分类';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.SVM:
                title = 'SVM';
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
            case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX:
                title = '混淆矩阵';
                break;
            case COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT:
                title = 'one-hot编码';
                break;
            case COMPONENT_TYPE.DATA_TOOLS.PYTHON_SCRIPT:
                title = 'Python脚本';
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
            case COMPONENT_TYPE.DATA_MERGE.STANDARD:
                description = `标准化组件，对源表的某列或多列进行标准化处理，处理后的数据存入新表。标准化采用的公式为（X-Mean)/(standard  deviation)。
                    Mean表示样本平均值，standard  deviation表示样本标准差。标准差=方差的算术平方根=s=sqrt(((x1-x)^2 +(x2-x)^2 +......(xn-x)^2)/n)。`;
                break;
            case COMPONENT_TYPE.DATA_MERGE.MISS_VALUE:
                description = `缺失值填充组件，是数据预处理的一环，当数据缺失时，可可采用填充方法补充数据，保障数据分析的准确性与效率。
                    常见的是对Null和空字符进行填充，可用最小值、最大值、平均值、0值、空字符串填充等，也支持自定义原值与替换值。`;
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
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS:
                description = 'GBDT二分类组件，是一种迭代的决策树算法，将多颗决策树的结果累加起来作为最终的预测输出。进行回归预测。';
                break;
            case COMPONENT_TYPE.MACHINE_LEARNING.SVM:
                description = `SVM组件，又称为支持向量机，是一类按监督学习方式对数据进行二元分类的广义线性分类器，决策边界是对学习样本求解的最大边距超平面。
                    SVM使用损失函数计算经验风险，并在求解系统中加入正则项已优化结构风险，是一个稳健的分类器。`;
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
            case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX:
                description = `混淆矩阵，是表示精度评价的一种标注格式，用n行n列的矩阵形式表示。其是一个可视化工具，常用于监督学习，在无监督学习一般叫做匹配矩阵。在图像精度评价中，主要用于比较分类结果和实际测得值，可以把分类结果的精度显示在一个混淆矩阵里面。
                    混淆矩阵的每一列代表了预测类别，每一列的总数表示预测为该类别的数据的数目；每一行代表了数据的真实归属类别，每一行的数据总数表示该类别的数据实例的数目。每一列中的数值表示真实数据被预测为该类的数目。`;
                break;
            case COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT:
                description = `one-hot编码组件，又称读热编码、一位有效编码。主要是采用N位状态寄存器来对N个状态进行编码，每个状态都由他独立的寄存器位，并且在任意时候只有一位有效。采用one-hot编码，将会使离散型特征之间的距离计算更加合理。
                    Example：
                    性别特征：["男","女"]，按照N位状态寄存器来对N个状态进行编码的原理，这里只有两个特征，所以N=2，处理之后如下：
                    男  =>  10
                    女  =>  01
                    运动特征：["足球"，"篮球"，"羽毛球"，"乒乓球"]（N=4）：
                    足球  =>  1000
                    篮球  =>  0100
                    羽毛球  =>  0010
                    乒乓球  =>  0001
                    离散特征进行one-hot编码，编码后的特征，每一维度的特征都可看做是连续的特征，可对每一维度特征进行归一化。`;
                break;
            case COMPONENT_TYPE.DATA_TOOLS.PYTHON_SCRIPT:
                description = `Python脚本组件，可自定义Python进行数据处理。`;
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
