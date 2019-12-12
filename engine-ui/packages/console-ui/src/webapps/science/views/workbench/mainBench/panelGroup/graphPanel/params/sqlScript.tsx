import * as React from 'react';
import { Collapse, Input, Button, message, Tooltip, Icon } from 'antd';
import { connect } from 'react-redux';
import Editor from 'widgets/editor';
import FullScreenButton from 'widgets/fullscreen';
import api from '../../../../../../api/experiment';
import MyIcon from '../../../../../../components/icon'
import { matchTaskParams } from '../../../../../../comm';
const Panel = Collapse.Panel;
const InputGroup = Input.Group;
@(connect((state: any) => {
    return {
        sysParams: state.common.sysParams || []
    }
}) as any)
class SqlScript extends React.Component<any, any> {
    state: any = {
        source: [{
            tableName: 'table1'
        }, {
            tableName: 'table2'
        }, {
            tableName: 'table3'
        }, {
            tableName: 'table4'
        }],
        dirty: false
    }
    static getDerivedStateFromProps (nextProps: any, prevState: any) {
        if (!prevState.dirty) {
            return {
                code: nextProps.data ? nextProps.data.sql : ''
            }
        } else {
            return null
        }
    }
    handleDdlChange = (value: any) => {
        this.setState({
            code: value,
            dirty: true
        })
    }
    handleFormat = () => {
        api.formatSql({ sql: this.state.code }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    code: res.data,
                    dirty: true
                });
            }
        })
    }
    handleSaveSql = () => {
        alert('sql change')
        const { currentTab, componentId, changeContent, data } = this.props;
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const sysParams = matchTaskParams(this.props.sysParams, this.state.code);
        const params: any = {
            ...currentComponentData.data,
            sqlComponent: {
                ...data,
                sql: this.state.code
            },
            taskVariables: sysParams
        }
        api.addOrUpdateTask(params).then((res: any) => {
            if (res.code == 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    renderSource = () => {
        const { data } = this.props;
        const { source } = this.state;
        return (
            <>
                {source.map((item: any, index: any) => {
                    return (
                        <InputGroup compact key={index} style={{ marginTop: -1 }}>
                            <Input style={{ width: '20%', background: '#fff' }} defaultValue={item.tableName} disabled />
                            <Input style={{ width: '80%', background: '#fff' }} disabled value={data.nodeList && data.nodeList[index]} />
                        </InputGroup>
                    )
                })}
            </>
        )
    }
    renderTooltips = () => {
        const title = '最后一句须为Select语句，作为此组件的输出';
        return <Tooltip overlayClassName="big-tooltip" title={title}>
            <Icon type="question-circle-o" className="supplementary" />
        </Tooltip>
    }
    render () {
        const { dirty } = this.state;
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    SQL脚本
                </div>
                <div className="params-single-tab-content">
                    <Collapse bordered={false} defaultActiveKey={['1', '2']} className="params-collapse">
                        <Panel header="输入源" key="1">
                            {this.renderSource()}
                        </Panel>
                        <Panel
                            header={
                                <div style={{ whiteSpace: 'nowrap' }}>
                                    SQL脚本
                                    {this.renderTooltips()}
                                </div>
                            }
                            {...{ id: 'sql-editor' }}
                            key="2"
                            style={{ height: 400, background: '#f5f5f5', paddingTop: 10 }}
                        >
                            <div>
                                <div className="toolbar ide-toolbar clear-offset" style={{ borderBottom: '1px solid #ddd' }}>
                                    <Button
                                        onClick={this.handleSaveSql}
                                    >
                                        <MyIcon style={{ width: 11, height: 11, marginRight: 5 }} type="save" />保存
                                    </Button>
                                    <FullScreenButton iconStyle={{ width: 12, height: 12, verticalAlign: 'top', marginRight: '0.4em' }} target="sql-editor" />
                                    <Button
                                        icon="appstore-o"
                                        onClick={this.handleFormat}
                                    >
                                        格式化
                                    </Button>
                                </div>
                                <Editor
                                    style={{ height: 'calc(100% - 29px)', minHeight: 300 }}
                                    sync={dirty}
                                    value={this.state.code}
                                    language="dtsql"
                                    onChange={this.handleDdlChange}
                                    options={{ readOnly: false, minimap: { enabled: false } }}
                                />
                            </div>
                        </Panel>
                    </Collapse>
                </div>
            </div>
        );
    }
}

export default SqlScript;
