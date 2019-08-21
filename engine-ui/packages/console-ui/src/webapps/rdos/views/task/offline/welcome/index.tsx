import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router'

import { TASK_TYPE, HELP_DOC_URL, ENGINE_SOURCE_TYPE } from '../../../../comm/const'
import {
    workbenchActions
} from '../../../../store/modules/offlineTask/offlineAction';

const mapStateToProps = function (state: any) {
    return {
        editor: state.editor,
        projectSuppoetEngines: state.projectSuppoetEngines
    };
};
@(connect(mapStateToProps, workbenchActions) as any)
class WelcomePage extends React.Component<any, any> {
    newTask (taskType: any) {
        this.props.setModalDefault({
            taskType
        });
        this.props.toggleCreateTask();
    }
    newDataSource = () => {
        this.props.router.push('/database')
    }
    uploadFile = () => {
        this.props.toggleUpload();
    }
    showExampleCode = () => {

    }
    render () {
        const { editor, projectSuppoetEngines } = this.props;
        const themeDark = editor.options.theme !== 'vs' ? true : undefined;
        const iconBaseUrl = themeDark ? '/public/rdos/img/theme-dark' : '/public/rdos/img/icon';
        const hasHadoop = projectSuppoetEngines.some((item: any) => item.value == ENGINE_SOURCE_TYPE.HADOOP)
        const taskType = hasHadoop ? TASK_TYPE.SQL : TASK_TYPE.LIBRASQL;
        return (
            <div className='c-welcome'>
                <section className='c-welcome__section'>
                    <img className='c-welcome__menu__logo' src={`${iconBaseUrl}/icon_datadevelop.png`} />
                    <div className='c-welcome__menu__menu'>
                        <header className='c-welcome__menu__title'>数据开发</header>
                        <nav className='c-welcome__menu__nav'>
                            <a onClick={this.newDataSource}>添加数据源</a>
                            <a onClick={this.newTask.bind(this, taskType)}>新建任务</a>
                            <a onClick={this.newTask.bind(this, TASK_TYPE.WORKFLOW)}>新建工作流</a>
                            {/* <a onClick={this.uploadFile}>上传本地文件</a> */}
                        </nav>
                    </div>
                </section>
                {
                    !window.APP_CONF.disableHelp
                        ? <section className='c-welcome__section'>
                            <img className='c-welcome__menu__logo' src={`${iconBaseUrl}/icon_more.png`} />
                            <div className='c-welcome__menu__menu'>
                                <header className='c-welcome__menu__title'>更多支持</header>
                                <nav className='c-welcome__menu__nav'>
                                    <a target="blank" href={HELP_DOC_URL.INDEX}>查看帮助文档</a>
                                </nav>
                            </div>
                        </section>
                        : null
                }
            </div>
        )
    }
}
export default withRouter(WelcomePage);
