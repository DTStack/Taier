import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import DefaultImgView from './defaultImgView';

import workbenchActions from '../../../../actions/workbenchActions';

@(connect(null, (dispatch: any) => {
    return {
        ...bindActionCreators(workbenchActions, dispatch)
    }
}) as any)
class DefaultExperimentView extends React.Component<any, any> {
    render () {
        return <DefaultImgView
            imgSrc='/public/science/img/experiment_default.svg'
            text='实验是一个可视化的算法建模作业，可将组件中的算法组件拖拽至画布区，组成流程化的算法任务。使用者可轻松、直观的完成模型搭建，并查看模型输出结果与预测结果。'
            imgButton={<div onClick={this.props.openNewExperiment} className='c-default-page__img__button'></div>}
        />
    }
}
export default DefaultExperimentView;
