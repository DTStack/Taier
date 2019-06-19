import React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';

import DefaultImgView from './defaultImgView';

import workbenchActions from '../../../../actions/workbenchActions';

@connect(null, dispatch => {
    return {
        ...bindActionCreators(workbenchActions, dispatch)
    }
})
class DefaultNotebookView extends React.Component {
    render () {
        return <DefaultImgView
            imgSrc='/public/science/img/notebook_default.svg'
            text='Notebook是一款交互式代码编写程序，可实现数据接入、数据清洗与转化、统计建模、机器学习、数据预测等多种开发需求，为开发者提供了灵活自由的开发环境和友好的操作体验。'
            imgButton={<div onClick={this.props.openNewNotebook} className='c-default-page__img__button'></div>}
        />
    }
}
export default DefaultNotebookView;
