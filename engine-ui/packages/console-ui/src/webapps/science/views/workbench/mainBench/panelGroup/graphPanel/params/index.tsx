/* eslint-disable no-unreachable */
import * as React from 'react';
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
import GbdtRegression from './gbdtRegression';
import RegressionClassification from './regressionClassification';
import UnionModel from './unionModel';
import KmeansUnion from './kmeansUnion';
import Standardization from './standardization';
import GdbtClass from './gdbtClass';
import MissValue from './missValue';
import SvmComponent from './svmComponent';
import ConfusionMatrix from './confusionMatrix';
import { isEmpty } from 'lodash';
import { bindActionCreators } from 'redux';
import * as experimentActions from '../../../../../../actions/experimentActions';
import OneHot from './onehot';
export const formItemLayout: any = {
    labelCol: {
        span: 24
    },
    wrapperCol: {
        span: 24
    }
};
@(connect((state: any) => {
    return {
        selectedCell: state.component.selectedCell,
        currentTabIndex: state.experiment.currentTabIndex,
        tabs: state.experiment.localTabs
    }
}, (dispatch: any) => {
    return bindActionCreators({ ...experimentActions }, dispatch);
}) as any)
class Params extends React.Component<any, any> {
    state: any = {
        lock: false
    }
    toggleLock = () => {
        this.setState((prev: any) => {
            return { lock: !prev.lock }
        });
    }
    shouldComponentUpdate (nextProps: any, nextState: any) {
        console.log(nextProps, nextState);
        if (nextState.lock) {
            return false
        }
        return true;
    }
    initRender = () => {
        const { selectedCell, changeContent, currentTabIndex, tabs } = this.props;
        if (isEmpty(selectedCell)) return '';
        const currentTab = tabs.find((o: any) => o.id == currentTabIndex);
        const componentId = selectedCell.data.id;
        let componentData: any = {};
        try {
            componentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id == componentId).data[TASK_ENUM[selectedCell.data.componentType]] || {};
        } catch (error) {
        }
        const componentProps: any = {
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
                return <OneHot key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_MERGE.TYPE_CHANGE:
                return <TypeChange key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_MERGE.NORMALIZE:
                return <Normalise key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_MERGE.STANDARD:
                return <Standardization key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_MERGE.MISS_VALUE:
                return <MissValue key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_PRE_HAND.DATA_SPLIT:
                return <DataSplit key={componentId} {...componentProps} />
            case COMPONENT_TYPE.MACHINE_LEARNING.LOGISTIC_REGRESSION:
                return <LogisticRegression key={componentId} {...componentProps} />
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION:
                return <GbdtRegression key={componentId} {...componentProps} />
            case COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION:
                return <KmeansUnion key={componentId} {...componentProps} />
            case COMPONENT_TYPE.MACHINE_LEARNING.GBDT_CLASS:
                return <GdbtClass key={componentId} {...componentProps} />
            case COMPONENT_TYPE.MACHINE_LEARNING.SVM:
                return <SvmComponent key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_PREDICT.DATA_PREDICT:
                return <DataPredict key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_EVALUATE.BINARY_CLASSIFICATION:
                return <BinaryClassfication key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_EVALUATE.REGRESSION_CLASSIFICATION:
                return <RegressionClassification key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_EVALUATE.UNION_CLASSIFICATION:
                return <UnionModel key={componentId} {...componentProps} />
            case COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX:
                return <ConfusionMatrix key={componentId} {...componentProps} />
            case COMPONENT_TYPE.FEATURE_ENGINEER.ONE_HOT:
                return <OneHot key={componentId} {...componentProps} />
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
