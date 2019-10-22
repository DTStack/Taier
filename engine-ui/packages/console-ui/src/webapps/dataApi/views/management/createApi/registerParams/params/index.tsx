import * as React from 'react';

import { Button, Tooltip, Icon, Checkbox } from 'antd';
import ConstColumnModel from '../../../../../model/constColumnModel';
import InputColumnModel from '../../../../../model/inputColumnModel';
import { resolveFormItemKey } from '../helper';
import { API_METHOD } from '../../../../../consts';
import Card from '../card';
import ConstTable from './const';
import InputTable from './input';
import Editor from 'widgets/editor';

class RegisterParams extends React.Component<any, any> {
    state: any = {
        sync: true
    }
    initColumns () {
        return [{
            dataIndex: 'name',
            title: '参数名称'
        }]
    }
    editorChange (value: any) {
        this.setState({
            sync: false
        })
        this.props.updateData({
            bodyDesc: value
        });
    }
    onReqHeaderChange = (checkedValue: any) => {
        this.props.updateData({
            containHeader: checkedValue.target.checked ? '1' : '0' // containHeader 0或空表示不包含、1 表示包含
        });
    }
    newColumn (type: any) {
        let { data = {} } = this.props;
        let { inputParam = [], constParam = [] } = data;
        let newColumn: any;
        if (type == 'in') {
            newColumn = new InputColumnModel();
            inputParam = [
                ...inputParam,
                newColumn
            ]
        } else if (type == 'const') {
            newColumn = new ConstColumnModel();
            constParam = [
                ...constParam,
                newColumn
            ]
        }
        newColumn && this.props.updateData({
            inputParam,
            constParam
        });
    }
    deleteColumn (type: any, id: any) {
        let { data = {} } = this.props;
        let { inputParam = [], constParam = [] } = data;
        let newColumns: any;
        if (type == 'in') {
            inputParam = [...inputParam];// shadow copy
            newColumns = inputParam;
        } else if (type == 'const') {
            constParam = [...constParam];// shadow copy
            newColumns = constParam;
        }
        const targetIndex = newColumns.findIndex((column: any) => {
            return column.id == id;
        })
        if (targetIndex > -1) {
            newColumns.splice(targetIndex, 1);
        }
        this.props.updateData({
            inputParam,
            constParam
        })
    }
    updateColumnData (type: any, values: any) {
        const keyAndValue = Object.entries(values);
        let { data = {} } = this.props;
        let { inputParam = [], constParam = [] } = data;
        let columns: any;
        let UpdateColumnClass: any;
        if (type == 'in') {
            inputParam = [...inputParam];// shadow copy
            columns = inputParam;
            UpdateColumnClass = InputColumnModel;
        } else if (type == 'const') {
            constParam = [...constParam];// shadow copy
            columns = constParam;
            UpdateColumnClass = ConstColumnModel;
        }
        keyAndValue.forEach(([key, value]) => {
            const { id, name } = resolveFormItemKey(key);
            let targetIndex = columns.findIndex((column: any) => {
                return column.id == id;
            })
            if (targetIndex > -1) {
                columns[targetIndex] = new UpdateColumnClass({
                    ...columns[targetIndex],
                    [name]: value
                })
            }
        });
        this.props.updateData({
            inputParam,
            constParam
        });
    }
    inputRef = React.createRef()
    constRef = React.createRef()
    validate = () => {
        return new Promise((resolve: any, reject: any) => {
            (this.inputRef.current as any).validateFieldsAndScroll({}, (err: any, values: any) => {
                if (!err) {
                    (this.constRef.current as any).validateFieldsAndScroll({}, (err: any, values: any) => {
                        if (!err) {
                            resolve(true)
                        } else {
                            resolve(false);
                        }
                    })
                } else {
                    resolve(false);
                }
            })
        })
    }
    render () {
        const { sync } = this.state;
        let { data = {}, method } = this.props;
        let { inputParam = [], constParam = [], bodyDesc } = data;
        const haveBody = method == API_METHOD.POST || method == API_METHOD.PUT;
        return (
            <React.Fragment>
                <Card
                    title='输入参数'
                    extra={(
                        <Button type='primary' onClick={this.newColumn.bind(this, 'in')}>新增参数</Button>
                    )}
                >
                    <InputTable
                        ref={this.inputRef as any}
                        method={method}
                        updateColumnData={this.updateColumnData.bind(this, 'in')}
                        deleteColumn={this.deleteColumn.bind(this, 'in')}
                        data={inputParam}
                    />
                </Card>
                <Card
                    style={{ marginTop: '40px' }}
                    title={<span>
                        常量参数
                        <Tooltip title="常量参数对用户不可见，用户调用API时无需传入，但后端服务始终接收列表中的常量参数及参数值。">
                            <Icon style={{ marginLeft: '5px' }} type="question-circle-o" />
                        </Tooltip>
                    </span>}
                    extra={(
                        <Button type='primary' onClick={this.newColumn.bind(this, 'const')}>新增参数</Button>
                    )}
                >
                    <ConstTable
                        ref={this.constRef as any}
                        deleteColumn={this.deleteColumn.bind(this, 'const')}
                        updateColumnData={this.updateColumnData.bind(this, 'const')}
                        data={constParam}
                    />
                </Card>
                {haveBody && (
                    <Card
                        style={{ marginTop: '40px' }}
                        title={<span>
                            请求Body描述
                            <Tooltip title="用户可自定义请求body，若定义了请求Body，此处body体将作为API调用者的请求样例，请求参数中位于body位置的参数将无效">
                                <Icon style={{ marginLeft: '5px' }} type="question-circle-o" />
                            </Tooltip>
                        </span>}
                    >
                        <Editor
                            sync={sync}
                            onChange={this.editorChange.bind(this)}
                            language='plaintext'
                            style={{
                                height: '218px',
                                minHeight: '218px',
                                border: '1px solid #DDDDDD'
                            }}
                            options={{ minimap: { enabled: false } }}
                            value={bodyDesc}
                        />
                    </Card>
                )}
                <div className="c-register-params__content__card " style={{ marginTop: '40px' }}>
                    <div className="c-register-params__content__card__head">
                        <span className="c-register-params__content__card__title">高级配置</span>
                    </div>
                    <div>
                        <Checkbox
                            checked={data.containHeader === '1'}
                            onChange={this.onReqHeaderChange}
                        >
                            返回结果中携带 Request Header 参数
                        </Checkbox>
                    </div>
                </div>
            </React.Fragment>
        )
    }
}
export default RegisterParams;
