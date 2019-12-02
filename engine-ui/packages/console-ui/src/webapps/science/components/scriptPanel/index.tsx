import * as React from 'react';
import { Button } from 'antd';
// 组件依赖全局组件
import Editor from 'widgets/editor';
import FullScreen from 'widgets/fullscreen';
import Icon from 'science/components/icon';

class ScriptPanel extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveScript = this.handleSaveScript.bind(this);
        this.handleFormat = this.handleFormat.bind(this);
        this.handleScriptChange = this.handleScriptChange.bind(this)
    }
    handleSaveScript () {
        debugger
        this.props.handleSave()
    }
    handleFormat () {
        this.props.handleFormat()
    }
    handleScriptChange (value: any) {
        this.props.handleChange(value)
    }
    render () {
        const { code, dirty, target, language, enable } = this.props;
        return (
            <div>
                <div className="toolbar ide-toolbar clear-offset" style={{ borderBottom: '1px solid #ddd' }}>
                    <Button
                        onClick={this.handleSaveScript}
                    >
                        <Icon
                            style={{ width: 11, height: 11, marginRight: 5 }}
                            type="save"
                        />保存
                    </Button>
                    <FullScreen
                        iconStyle={{ width: 12, height: 12, verticalAlign: 'top', marginRight: '0.4em' }}
                        target={target}
                    />
                    {enable &&
                        <Button
                            icon="appstore-o"
                            onClick={this.handleFormat}
                        >
                            格式化
                        </Button>
                    }
                </div>
                <Editor
                    style={{ height: 'calc(100% - 29px)', minHeight: 300 }}
                    sync={dirty}
                    value={code}
                    language={language}
                    onChange={this.handleScriptChange}
                    options={{ readOnly: false, minimap: { enabled: false } }}
                />
            </div>
        )
    }
}

export default ScriptPanel;
