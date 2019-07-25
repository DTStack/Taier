import React from 'react';
import { Tabs, Icon } from 'antd';
import { connect } from 'react-redux';

import NotebookSiderBar from './tabPanes/notebook';
import ComponentSiderBar from './tabPanes/component';
import ExperimentSiderBar from './tabPanes/experiment';
import commonActionType from '../../../consts/commonActionType';
import { siderBarType } from '../../../consts'
import { initLoadTreeNode } from '../../../actions/base/fileTree';
import utils from 'utils';

const TabPane = Tabs.TabPane;
@connect((state) => {
    return {
        siderBarKey: state.common.siderBarKey,
        files: {
            notebook: state.notebook.files,
            experiment: state.experiment.files,
            component: state.component.files,
            resource: state.resource.files
        }
    }
}, (dispatch) => {
    return {
        changeSiderBar (key) {
            dispatch({
                type: commonActionType.CHANGE_SIDERBAR_KEY,
                payload: key
            })
            location.href = location.href.split('?')[0] + '?siderkey=' + key;
        },
        initLoadTreeNode (...args) {
            return dispatch(initLoadTreeNode(...args));
        }
    }
})
class SiderBarContainer extends React.Component {
    componentDidMount () {
        const { files } = this.props;
        const siderkey = utils.getParameterByName('siderkey');
        if (siderkey) {
            this.props.changeSiderBar(siderkey);
        }
        if (!files.notebook.length && !files.resource.length && !files.experiment.length && !files.component.length) {
            this.props.initLoadTreeNode();
        }
    }
    render () {
        const { changeSiderBar, siderBarKey } = this.props;
        return (
            <Tabs activeKey={siderBarKey} onChange={changeSiderBar} className={`c-antd-tabs-sidebar ${siderBarKey == siderBarType.model ? 'u-pane--hidden' : ''}`} tabPosition='left'>
                <TabPane tab={<span><Icon className='c-antd-tabs-sidebar__tab__icon c-antd-tabs-sidebar__tab__icon--inline' type='book' /><span className='c-antd-tabs-sidebar__text--inline'>Notebook</span></span>} key={siderBarType.notebook}>
                    <NotebookSiderBar />
                </TabPane>
                <TabPane tab={<span><Icon className='c-antd-tabs-sidebar__tab__icon' type='usb' />实验</span>} key={siderBarType.experiment}>
                    <ExperimentSiderBar />
                </TabPane>
                <TabPane tab={<span><img className='c-antd-tabs-sidebar__tab__img__icon' src='/public/science/img/icon/component.svg' />组件</span>} key={siderBarType.component}>
                    <ComponentSiderBar />
                </TabPane>
                <TabPane tab={<span><img className='c-antd-tabs-sidebar__tab__img__icon' src='/public/science/img/icon/model.svg' />模型</span>} key={siderBarType.model}>model</TabPane>
            </Tabs>
        )
    }
}
export default SiderBarContainer;
