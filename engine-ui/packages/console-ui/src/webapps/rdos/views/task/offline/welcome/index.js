import React from 'react';
import { connect } from 'react-redux';

import { TASK_TYPE, HELP_DOC_URL } from '../../../../comm/const'
import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction';

const mapStateToProps = function (state) {
    return {
        editor: state.editor
    };
};
@connect(mapStateToProps, workbenchActions)
class WelcomePage extends React.Component {
    newTask (taskType) {
        this.props.setModalDefault({
            taskType
        });
        this.props.toggleCreateTask();
    }
    newDataSource = () => {

    }
    uploadFile = () => {
        this.props.toggleUpload();
    }
    showExampleCode = () => {

    }
    render () {
        const { editor } = this.props;
        const themeDark = editor.options.theme !== 'vs' ? true : undefined;
        const iconBaseUrl = themeDark ? '/public/rdos/img/theme-dark' : '/public/rdos/img/icon';
        return (
            <div className='c-welcome'>
                <section className='c-welcome__section'>
                    <img className='c-welcome__menu__logo' src={`${iconBaseUrl}/icon_datadevelop.png`} />
                    <div className='c-welcome__menu__menu'>
                        <header className='c-welcome__menu__title'>数据开发</header>
                        <nav className='c-welcome__menu__nav'>
                            <a onClick={this.newDataSource}>添加数据源</a>
                            <a onClick={this.newTask.bind(this, TASK_TYPE.SYNC)}>新建同步任务</a>
                            <a onClick={this.newTask.bind(this, TASK_TYPE.SQL)}>新建SparkSQL任务</a>
                            <a onClick={this.newTask.bind(this, TASK_TYPE.WORKFLOW)}>新建工作流</a>
                            <a onClick={this.uploadFile}>上传本地文件</a>
                        </nav>
                    </div>
                </section>
                <section className='c-welcome__section'>
                    <img className='c-welcome__menu__logo' src={`${iconBaseUrl}/icon_more.png`} />
                    <div className='c-welcome__menu__menu'>
                        <header className='c-welcome__menu__title'>更多支持</header>
                        <nav className='c-welcome__menu__nav'>
                            {/* <a onClick={this.showExampleCode}>查看示例代码</a> */}
                            <a target="blank" href={HELP_DOC_URL.INDEX}>查看帮助文档</a>
                        </nav>
                    </div>
                </section>
            </div>
        )
    }
}
export default WelcomePage;
