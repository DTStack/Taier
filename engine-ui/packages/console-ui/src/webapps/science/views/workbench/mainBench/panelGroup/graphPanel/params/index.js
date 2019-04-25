import React, { Component } from 'react';
import { connect } from 'react-redux';
import { COMPONENT_TYPE } from '../../../../../../consts'
import ReadDatabase from './readDatabase';
import WriteDatabase from './writeDatabase';
import SqlScript from './sqlScript';
import TypeChange from './typeChange';
import Normalise from './normalise';
import DataSplit from './dataSplit';
import LogisticRegression from './logisticRegression';
import DataPredict from './dataPredict';
import BinaryClassfication from './binaryClassfication';
import { isEmpty } from 'lodash';
export const formItemLayout = {
    labelCol: {
        span: 24
    },
    wrapperCol: {
        span: 24
    }
};
const TASK_ENUM = {
    [COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE]: 'readTableComponent',
    [COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE]: 'writeTableComponent',
    [COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT]: 'sqlComponent',
    [COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE]: 'transTypeComponent',
    [COMPONENT_TYPE.DATA_MERGE.NORMALIZE]: 'normalizationComponent',
    [COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT]: 'dataSplitComponent',
    [COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION]: 'logisticComponent',
    [COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT]: 'predictComponent',
    [COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION]: 'eveluationComponent'
}
@connect(state => {
    return {
        selectedCell: state.component.selectedCell
    }
})
class Params extends Component {
    shouldComponentUpdate (nextProps, nextState) {
        const selectedCell = nextProps.selectedCell;
        if (!isEmpty(selectedCell) && selectedCell.mxObjectId !== this.props.selectedCell.mxObjectId) {
            return true
        }
        return false;
    }
    initRender = () => {
        const { selectedCell, data, taskId } = this.props;
        if (isEmpty(selectedCell)) return '';
        const componentData = data[TASK_ENUM[selectedCell.data.taskType]] || {};
        switch (selectedCell.data.taskType) {
            case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE:
                return (
                    <ReadDatabase data={componentData} />
                )
            case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE:
                return (
                    <WriteDatabase data={componentData} />
                )
            case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT:
                return <SqlScript data={componentData} />
            case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE:
                return <TypeChange data={componentData} />
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
                return <Normalise data={componentData} />
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT:
                return <DataSplit data={componentData} taskId={taskId} />
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION:
                return <LogisticRegression data={componentData} taskId={taskId} />
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT:
                return <DataPredict data={componentData} />
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
                return <BinaryClassfication data={componentData} taskId={taskId} />
            default:
                return ''
        }
    }
    render () {
        return (
            <div className="tab-content">
                {this.initRender()}
            </div>
        );
    }
}
export default Params;
