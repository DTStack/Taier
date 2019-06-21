/* eslint-disable no-unreachable */
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { COMPONENT_TYPE, TASK_ENUM } from '../../../../../../consts'
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
import { bindActionCreators } from 'redux';
import * as experimentActions from '../../../../../../actions/experimentActions';
export const formItemLayout = {
    labelCol: {
        span: 24
    },
    wrapperCol: {
        span: 24
    }
};
@connect(state => {
    return {
        selectedCell: state.component.selectedCell,
        currentTabIndex: state.experiment.currentTabIndex,
        tabs: state.experiment.localTabs
    }
}, (dispatch) => {
    return bindActionCreators({ ...experimentActions }, dispatch);
})
class Params extends Component {
    state = {
        lock: false
    }
    toggleLock = () => {
        this.setState((prev) => {
            return { lock: !prev.lock }
        });
    }
    shouldComponentUpdate (nextProps, nextState) {
        console.log(nextProps, nextState);
        if (nextState.lock) {
            return false
        }
        return true;
    }
    initRender = () => {
        const { selectedCell, changeContent, currentTabIndex, tabs } = this.props;
        if (isEmpty(selectedCell)) return '';
        const currentTab = tabs.find(o => o.id == currentTabIndex);
        const componentId = selectedCell.data.id;
        let componentData = {};
        try {
            componentData = currentTab.graphData.find(o => o.vertex && o.data.id == componentId).data[TASK_ENUM[selectedCell.data.componentType]] || {};
        } catch (error) {
        }
        const componentProps = {
            data: componentData,
            changeContent,
            currentTab,
            componentId,
            toggleLock: this.toggleLock
        }
        switch (selectedCell.data.componentType) {
            case COMPONENT_TYPE.DATA_SOURCE.READ_DATABASE:
                return (
                    <ReadDatabase key={componentId} {...componentProps} />
                )
            case COMPONENT_TYPE.DATA_SOURCE.WRITE_DATABASE:
                return (
                    <WriteDatabase key={componentId} {...componentProps} />
                )
            case COMPONENT_TYPE.DATA_TOOLS.SQL_SCRIPT:
                return <SqlScript key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE:
                return <TypeChange key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
                return <Normalise key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT:
                return <DataSplit key={componentId} {...componentProps} />
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION:
                return <LogisticRegression key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT:
                return <DataPredict key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
                return <BinaryClassfication key={componentId} {...componentProps} />
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
