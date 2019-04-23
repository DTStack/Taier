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
@connect(state => {
    return {
        selectedCell: state.component.selectedCell
    }
})
class Params extends Component {
    shouldComponentUpdate (nextProps, nextState) {
        const selectedCell = nextProps.selectedCell;
        if (!this.isEmptyObejct(selectedCell) && selectedCell.mxObjectId !== this.props.selectedCell.mxObjectId) {
            return true
        }
        return false;
    }
    isEmptyObejct = (obejct) => {
        return Object.keys(obejct).length === 0
    }
    initRender = () => {
        const { selectedCell } = this.props;
        if (this.isEmptyObejct(selectedCell)) return <BinaryClassfication />;
        switch (selectedCell.data.taskType) {
            case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE:
                return (
                    <ReadDatabase />
                )
            case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE:
                return (
                    <WriteDatabase />
                )
            case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT:
                return <SqlScript />
            case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE:
                return <TypeChange />
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
                return <Normalise />
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT:
                return <DataSplit />
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION:
                return <LogisticRegression />
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT:
                return <DataPredict />
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
                return <BinaryClassfication />
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
