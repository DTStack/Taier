import React, { Component } from 'react';
import ReadDatabase from './readDatabase';
import WriteDatabase from './writeDatabase';
import SqlScript from './sqlScript'
import { connect } from 'react-redux';
import { COMPONENT_TYPE } from '../../../../../../consts'
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
        if (this.isEmptyObejct(selectedCell)) return <SqlScript />;
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
                return ''
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
                return ''
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT:
                return ''
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION:
                return ''
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT:
                return ''
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
                return ''
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
