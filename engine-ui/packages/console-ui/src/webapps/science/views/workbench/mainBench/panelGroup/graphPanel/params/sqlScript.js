import React, { Component } from 'react';
import { Collapse, Input, Button } from 'antd';
import Editor from 'widgets/editor';
import FullScreenButton from 'widgets/fullscreen';
import MyIcon from '../../../../../../components/icon'
const Panel = Collapse.Panel;
const InputGroup = Input.Group;
class SqlScript extends Component {
    state = {
        source: [{
            tableName: 'table1'
        }, {
            tableName: 'table2'
        }],
        code: ''
    }
    renderSource = () => {
        const { source } = this.state;
        return (
            <>
                {source.map((item, index) => {
                    return (
                        <InputGroup compact key={index} style={{ marginTop: -1 }}>
                            <Input style={{ width: '20%' }} defaultValue={item.tableName} disabled />
                            <Input style={{ width: '80%' }} />
                        </InputGroup>
                    )
                })}
            </>
        )
    }
    render () {
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    SQL脚本
                </div>
                <div className="params-single-tab-content">
                    <Collapse bordered={false} defaultActiveKey={['1', '2']} className="params-collapse">
                        <Panel header="输入源" key="1">
                            { this.renderSource() }
                        </Panel>
                        <Panel header={
                            <div style={{ whiteSpace: 'nowrap' }}>
                                SQL脚本
                                <span className="supplementary">最后一句须为Select语句，作为此组件的输出</span>
                            </div>
                        } key="2">
                            <div className="toolbar ide-toolbar clear-offset">
                                <Button
                                    // onClick={this.saveTab.bind(this, true, 'button')}
                                    title="保存任务"
                                >
                                    <MyIcon className="my-icon" type="save" />保存
                                </Button>
                                <FullScreenButton />
                                <Button
                                    icon="appstore-o"
                                    title="格式化"
                                    // onClick={onFormat}
                                >
                                    格式化
                                </Button>
                            </div>
                            <Editor
                                value={this.state.code}
                                language="dtsql"
                                options={{ readOnly: false, minimap: { enabled: false } }}
                            />
                        </Panel>
                    </Collapse>
                </div>
            </div>
        );
    }
}

export default SqlScript;
