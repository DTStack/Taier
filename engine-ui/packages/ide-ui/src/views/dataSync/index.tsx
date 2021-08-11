import * as React from 'react'
import PropTypes from 'prop-types'
import SplitPane from 'react-split-pane'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import DataSync from './dataSync'
import { workbenchActions } from '../../controller/dataSync/offlineAction'
import * as editorActions from '../../controller/dataSync/workbench'

const propType: any = {
    editor: PropTypes.object,
    toolbar: PropTypes.object,
    console: PropTypes.object
}
const initialState = {
    changeTab: true,
    size: undefined,
    runTitle: 'Command/Ctrl + R'
}
type Istate = typeof initialState;

@(connect(
    (state: any) => {
        const { workbench, dataSync } = state.dataSync
        const { currentTab, tabs } = workbench
        const currentTabData = tabs.filter((tab: any) => {
            return tab.id === currentTab
        })[0]

        return {
            editor: state.editor,
            project: state.project,
            user: state.user,
            currentTab,
            currentTabData,
            dataSync
        }
    },
    (dispatch: any) => {
        const taskAc = workbenchActions(dispatch)
        const editorAc = bindActionCreators(editorActions, dispatch)
        const actions = Object.assign(editorAc, taskAc)
        return actions
    }
) as any)
class DataSyncWorkbench extends React.Component<any, Istate> {
    state = {
        changeTab: true,
        size: undefined,
        runTitle: 'Command/Ctrl + R'
    };

    static propTypes = propType;
    componentDidMount () {
        const currentNode = this.props.currentTabData
        if (currentNode) {
            this.props.getTab(currentNode.id) // 初始化console所需的数据结构
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const current = nextProps.currentTabData
        const old = this.props.currentTabData
        if (current && current.id !== old.id) {
            this.props.getTab(current.id)
        }
    }

    changeTab = (state: any) => {
        let changeTab = false
        if (state) {
            changeTab = true
        } else {
            changeTab = false
        }

        this.setState({
            changeTab
        })
    };

    render () {
        const { currentTabData } = this.props

        return (
            <div className="ide-editor">
                <div style={{ zIndex: 901 }} className="ide-content">
                    <SplitPane
                        split="horizontal"
                        minSize={100}
                        maxSize={-77}
                        primary="first"
                        key={'ide-split-pane'}
                    >
                        <div
                            style={{
                                width: '100%',
                                height: '100%',
                                minHeight: '400px',
                                position: 'relative'
                            }}
                        >
                            <DataSync currentTabData={currentTabData} />
                        </div>
                    </SplitPane>
                </div>
            </div>
        )
    }
}

export default DataSyncWorkbench
