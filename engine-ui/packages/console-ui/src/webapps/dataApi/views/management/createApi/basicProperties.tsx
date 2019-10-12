import * as React from 'react'
import { Form, Input, Button, Select, Card, Cascader, message, Row, Col, notification } from 'antd';

import DataSourceTable from './dataSourceTable'
import HelpDoc from '../../helpDoc';
import { formItemLayout, API_METHOD, SECURITY_TYPE, PARAMS_POSITION, FIELD_TYPE } from '../../../consts'
import NewGroupModal from '../../../components/newGroupModal';
import api from '../../../api/apiManage'

import InputColumnModel, { inputColumnsKeys } from '../../../model/inputColumnModel';
import utils from 'utils';
import { parseParamsPath } from './helper';

const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;
class ManageBasicProperties extends React.Component<any, any> {
    state: any = {
        dataSource: [],
        tableList: [],
        tableDetailList: {},
        loading: false
    }
    componentDidMount () {
        this.getDataSource();
    }
    /**
     * 校验后端path的参数和api path的参数是否一致
     * @param {*} values fields
     */
    checkParamsPath (values: any, isSilent: any) {
        const { APIPath, originalPath } = values;
        let pathParams = parseParamsPath(APIPath);
        let originPathParams = parseParamsPath(originalPath);
        let tmpMap: any = {};
        /**
         * 校验两边的参数是否一致
         */
        originPathParams.forEach((param: any) => {
            tmpMap[param] = tmpMap[param] ? ++tmpMap[param] : 1;
        });
        let isPass = pathParams.every((param: any) => {
            if (tmpMap[param]) {
                tmpMap[param] = --tmpMap[param];
                if (tmpMap[param] == 0) {
                    delete tmpMap[param];
                }
                return true;
            }
            return false;
        })
        if (isPass && !Object.keys(tmpMap).length) {
            return true;
        } else {
            !isSilent && notification.error({
                description: 'API path与后端 Path 的参数不一致！',
                message: '配置错误'
            });
            return false
        }
    }
    /**
     * 根据path更新一下参数
     */
    updateRegisterColumns (path: any) {
        const { registerParams } = this.props;
        let { inputParam = [] } = registerParams;
        let params: any[] = parseParamsPath(path);
        params = [...(new Set(params) as any)];
        /**
        * 删除源数据已不存在的path参数
        */
        inputParam = inputParam.map((column: any) => {
            let name = column[inputColumnsKeys.NAME];
            let position = column[inputColumnsKeys.POSITION];

            if (!params.includes(name) && position == PARAMS_POSITION.PATH) {
                return null;
            }
            return column;
        }).filter(Boolean)
        /**
         * 获取现存的参数
         */
        let existNames = inputParam.map((column: any) => {
            return column[inputColumnsKeys.NAME];
        })
        /**
         * 向源数据添加不存在的path param
         */
        params = params.map((param: any) => {
            let targetIndex = existNames.indexOf(param);
            if (targetIndex == -1 || inputParam[targetIndex][inputColumnsKeys.POSITION] != PARAMS_POSITION.PATH) {
                /**
                 * 假如path中的参数不存在于源数据中，或者存在，但是不是path类型
                 */
                return new InputColumnModel({
                    [inputColumnsKeys.NAME]: param,
                    [inputColumnsKeys.POSITION]: PARAMS_POSITION.PATH,
                    [inputColumnsKeys.TYPE]: FIELD_TYPE.STRING
                });
            }
        }).filter(Boolean);
        this.props.changeRegisterParams({
            ...registerParams,
            inputParam: [
                ...params,
                ...inputParam
            ]
        }, true);
    }
    pass () {
        const { isRegister } = this.props;
        this.props.form.validateFields(async (err: any, values: any) => {
            if (!err) {
                if (isRegister) {
                    const isHostVaild = await this.checkHostVaild();
                    if (!isHostVaild) {
                        return false;
                    }
                    if (values.APIPath && !(this as any).checkParamsPath(values)) {
                        return false;
                    }
                    this.updateRegisterColumns(values.originalPath)
                }
                this.props.dataChange({
                    ...values
                })
            }
        });
    }
    cancelAndSave () {
        const { isRegister } = this.props;
        const { validateFields, getFieldsValue } = this.props.form;
        const params = getFieldsValue();
        this.setState({
            loading: true
        })
        validateFields(['APIName'], {}, (error: any, values: any) => {
            if (!error) {
                if (isRegister) {
                    if (params.APIPath && this.checkParamsPath(params, true)) {
                        this.updateRegisterColumns(params.originalPath)
                    }
                }
                this.props.cancelAndSave({ ...params }).then(() => {
                    this.setState({
                        loading: false
                    })
                }).catch(() => {
                    this.setState({
                        loading: false
                    })
                });
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }
    // 数据源改变，获取表
    dataSourceChange (key: any) {
        this.setState({
            showTable: false
        })
        this.props.form.setFieldsValue({
            table: ''
        })

        this.props.tablelist(key)
            .then(
                (res: any) => {
                    if (res) {
                        this.setState({
                            tableList: res.data
                        })
                    }
                }
            )
    }
    // 表改变
    tableChange () {
        this.setState({
            showTable: false
        })
    }
    getTableListView () {
        const data = this.state.tableList;
        return data.map(
            (item: any, index: any) => {
                return <Option key={item}>{item}</Option>
            }
        )
    }
    getSecurityListView () {
        const data = this.props.apiManage.securityList;
        return data.map(
            (item: any, index: any) => {
                return <Option value={item.id} key={item.id}>{`${item.name} (${item.type == SECURITY_TYPE.BLACK ? '黑名单' : '白名单'})`}</Option>
            }
        )
    }
    onSourcePreview () {
        const dataSource = this.props.form.getFieldValue('dataSource');
        const tableValue = this.props.form.getFieldValue('table');
        if (!dataSource || !tableValue) {
            message.error('请选择数据表！')
            return;
        }
        this.setState({
            showTable: !this.state.showTable
        }, () => {
            if (this.state.showTable) {
                this.getTableDetail();
            }
        })
    }
    getDataSourceOptionView () {
        const data = this.state.dataSource;
        return data.map(
            (item: any) => {
                return <Option key={item.id}>{item.name}</Option>
            }
        )
    }
    getDataSource () {
        this.props.getDataSourceList(null)
            .then(
                (res: any) => {
                    if (res) {
                        this.setState({
                            dataSource: res.data
                        })
                    }
                }
            )
    }
    getTableDetail () {
        const dataSource = this.props.form.getFieldValue('dataSource');
        const tableValue = this.props.form.getFieldValue('table');
        this.setState({
            tableDetailList: [],
            tableLoading: true
        })
        this.props.previewData(dataSource, tableValue)
            .then(
                (res: any) => {
                    if (res) {
                        this.setState({
                            tableDetailList: res.data,
                            tableLoading: false
                        })
                    }
                }
            )
    }
    getTable () {
        if (this.state.showTable) {
            return (
                <Card
                    className="box-2"
                    style={{ marginTop: '10px' }}
                    noHovering>
                    <DataSourceTable loading={this.state.tableLoading} data={this.state.tableDetailList}></DataSourceTable>
                </Card>)
        }
        return null;
    }
    getCatagoryOption () {
        const tree = this.props.apiMarket.apiCatalogue;

        function exchangeTree (data: any) {
            let arr: any = []

            if (!data || data.length < 1) {
                return null;
            }
            for (let i = 0; i < data.length; i++) {
                let item = data[i];

                if (item.api) {
                    return null;
                }
                arr.push({
                    value: item.id,
                    label: item.catalogueName,
                    children: exchangeTree(item.childCatalogue)
                })
            }
            return arr;
        }

        return exchangeTree(tree) || [];
    }
    showNewGroup () {
        this.setState({
            newGroupModalShow: true
        })
    }
    hideNewGroup () {
        this.setState({
            newGroupModalShow: false
        })
    }
    groupChange (value: any) {
        const tree = this.props.apiMarket.apiCatalogue;
        let arr: any = [];

        function exchangeTree (data: any) {
            if (!data || data.length < 1) {
                return null;
            }

            for (let i = 0; i < data.length; i++) {
                let item = data[i];

                if (item.id == value && !item.api) {
                    arr.push(item.id);
                    return item.id;
                }
                if (exchangeTree(item.childCatalogue)) {
                    arr.push(item.id);
                    return item.id
                }
            }
            return null;
        }

        if (exchangeTree(tree)) {
            this.props.form.setFieldsValue({
                APIGroup: arr.reverse()
            })
        }
    }
    renderMethod () {
        const { isRegister } = this.props;
        /**
         * 生成API的列表，去除put和delete
         */
        const whiteList: any = [API_METHOD.POST, API_METHOD.GET];
        let arr: any = [];
        for (let key in API_METHOD) {
            let value = API_METHOD[key];
            if (!isRegister && !whiteList.includes(value)) {
                continue;
            }
            arr.push(<Option value={value}>{key}</Option>)
        }
        return arr;
    }
    checkNameExist (rule: any, value: any, callback: any) {
        if (value) {
            if ((this as any)._checkClockObj) {
                clearTimeout((this as any)._checkClockObj.clock);
                (this as any)._checkClockObj.callback();
            }
            (this as any)._checkClockObj = {};
            (this as any)._checkClockObj.callback = callback;
            (this as any)._checkClockObj.clock = setTimeout(() => {
                api.checkNameExist({
                    name: value,
                    apiId: utils.getParameterByName('apiId')
                })
                    .then(
                        (res: any) => {
                            if (res.data || res.code != 1) {
                                callback()
                            } else {
                                const error = '名称已存在';
                                callback(error);
                            }
                        }
                    ).catch(() => {
                        this.setState({
                            loading: false
                        })
                    })
            }, 500)
        } else {
            callback()
        }
    }
    checkHostVaild () {
        const form = this.props.form;
        const host = form.getFieldValue('originalHost');
        const path = form.getFieldValue('originalPath');
        return api.checkHostVaild({
            host,
            path,
            apiId: utils.getParameterByName('apiId')
        }).then((res: any) => {
            if (res.code == 1) {
                return true;
            } else {
                return false;
            }
        })
    }
    changeMethod (value: any) {
        const oldValue = this.props.form.getFieldValue('method');
        const { registerParams, isRegister } = this.props;
        // 当从有body参数切换到无body参数的时候，清除body参数
        if (isRegister && (value == API_METHOD.GET || value == API_METHOD.DELETE) && (oldValue == API_METHOD.POST || oldValue == API_METHOD.PUT)) {
            let inputParam = registerParams.inputParam || [];
            inputParam = inputParam.filter((param: any) => {
                return param[inputColumnsKeys.POSITION] != PARAMS_POSITION.BODY
            });
            this.props.changeRegisterParams({
                ...registerParams,
                bodyDesc: null,
                inputParam: inputParam
            }, true)
        }
    }
    render () {
        const { isRegister } = this.props;
        const { getFieldDecorator } = this.props.form
        const options = this.getCatagoryOption();
        const { newGroupModalShow, loading } = this.state;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <Row>
                            <Col className="form-title-line" {...formItemLayout.labelCol}>
                                <span className='form-title title-border-l-blue'>基本信息</span>
                            </Col>
                        </Row>
                        <FormItem
                            {...formItemLayout}
                            label="所属分组"
                        >

                            {getFieldDecorator('APIGroup', {
                                rules: [
                                    { required: true, message: '请选择分组' }
                                ],
                                initialValue: this.props.APIGroup
                            })(
                                <Cascader style={{ width: '85%' }} showSearch options={options} placeholder="请选择分组" />
                            )
                            }
                            <a style={{ paddingLeft: '8px' }} onClick={this.showNewGroup.bind(this)} >新建分组</a>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="API名称"
                        >
                            {getFieldDecorator('APIName', {
                                rules: [
                                    { required: true, message: '请输入API名称' },
                                    { min: 2, message: '最小字数不能少于2' },
                                    { max: 64, message: '最大字数不能超过64' },
                                    { pattern: new RegExp(/^(\w*)$/), message: 'API名字只能以字母，数字，下划线组成' },
                                    {
                                        validator: this.checkNameExist.bind(this)
                                    }],
                                initialValue: this.props.APIName,
                                validateFirst: true
                            })(
                                <Input style={{ width: '85%' }} />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="API描述"
                        >
                            {getFieldDecorator('APIdescription', {
                                rules: [
                                    { required: false, message: '请输入API描述' },
                                    { max: 200, message: '最大字符不能超过200' }],
                                initialValue: this.props.APIdescription
                            })(
                                <TextArea autosize={{ minRows: 3, maxRows: 5 }} style={{ width: '85%' }} />
                            )}
                        </FormItem>
                        {isRegister && (
                            <React.Fragment>
                                <FormItem
                                    {...formItemLayout}
                                    label="后端 Host"
                                >
                                    {getFieldDecorator('originalHost', {
                                        rules: [
                                            { required: true, message: '请输入后端 Host' },
                                            { pattern: new RegExp(/^(http|https):\/\/[\w|.|\-|:]+$/), message: '请填写正确的host' }],
                                        initialValue: this.props.originalHost
                                    })(
                                        <Input placeholder='http(s)://host:port' style={{ width: '85%' }} />
                                    )}
                                    <span style={{ marginLeft: '5px' }}>
                                        <HelpDoc doc="registerApiHost" />
                                    </span>
                                </FormItem>
                                <FormItem
                                    {...formItemLayout}
                                    label="后端服务 Path"
                                >
                                    {getFieldDecorator('originalPath', {
                                        rules: [
                                            { required: true, message: '请输入后端 Path' },
                                            { max: 200, message: '最大字符不能超过200' },
                                            { min: 2, message: '最小字符不能小于2' },
                                            {
                                                pattern: this.props.form.getFieldValue('protocol') === 'HTTP/HTTPS'
                                                    ? new RegExp(/^\/[^\s\u4e00-\u9fa5]+$/) : null,
                                                message: '须以/开头，限制2-100个字符，不支持中文，空格'
                                            }
                                        ],
                                        initialValue: this.props.originalPath
                                    })(
                                        <Input style={{ width: '85%' }} />
                                    )}
                                    <span style={{ marginLeft: '5px' }}>
                                        <HelpDoc doc="registerApiPath" />
                                    </span>
                                </FormItem>
                            </React.Fragment>
                        )}
                        <FormItem
                            {...formItemLayout}
                            label="API path"
                        >
                            {getFieldDecorator('APIPath', {
                                rules: [
                                    { max: 200, message: '最大字符不能超过200' },
                                    { min: 2, message: '最小字符不能小于2' },
                                    { pattern: isRegister ? new RegExp(/^(\/(\{[-\w]+\}|[-\w]+))*(\/)?$/) : new RegExp(/^(\/[-|\w]+)+$/), message: '支持英文，数字，下划线，连字符(-)，限制2—200个字符，只能 / 开头，如/user' },
                                    isRegister ? {} : { pattern: new RegExp(/^(([^/]*\/[^/]*){1,2}|[^/]*)$/), message: '最多支持两层路径' }],
                                initialValue: this.props.APIPath
                            })(
                                <Input style={{ width: '85%' }} />
                            )}
                            <span style={{ marginLeft: '5px' }}>
                                <HelpDoc doc={isRegister ? 'registerApiPathInfo' : 'apiPathInfo'} />
                            </span>
                        </FormItem>
                        <Row style={{ marginTop: '40px' }}>
                            <Col className="form-title-line" {...formItemLayout.labelCol}>
                                <span className='form-title title-border-l-blue'>API参数</span>
                            </Col>
                        </Row>
                        <FormItem
                            {...formItemLayout}
                            label="协议"
                        >
                            {getFieldDecorator('protocol', {
                                initialValue: this.props.protocol || 'HTTP/HTTPS'
                            })(
                                <Select
                                    style={{ width: '85%' }}
                                    onChange={(value: any) => {
                                        if (value === 'WebService') {
                                            this.props.form.setFieldsValue({
                                                method: 1,
                                                originalPath: this.props.form.getFieldValue('originalPath')
                                            });
                                        }
                                    }}
                                >
                                    <Option value="HTTP/HTTPS">HTTP/HTTPS</Option>
                                    {
                                        isRegister
                                            ? (
                                                <Option value="WebService">WebService</Option>
                                            )
                                            : null
                                    }
                                </Select>
                                // <Input disabled  style={{ width: '85%' }}/>
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="请求方式"
                        >
                            {getFieldDecorator('method', {
                                rules: [{ required: true, message: '请选择请求方式' }],
                                initialValue: (this.props.method || this.props.method == 0) ? this.props.method : API_METHOD.POST
                            })(
                                <Select onChange={this.changeMethod.bind(this)} style={{ width: '85%' }} disabled={this.props.form.getFieldValue('protocol') === 'WebService'} >
                                    {this.renderMethod()}
                                </Select>
                            )}
                        </FormItem>
                        {!isRegister && (
                            <FormItem
                                {...formItemLayout}
                                label="返回类型"
                            >
                                {getFieldDecorator('responseType', {
                                    rules: [{ required: true, message: '请选择返回类型' }],
                                    initialValue: 'JSON'
                                })(
                                    <Select style={{ width: '85%' }}>
                                        <Option value="JSON">JSON</Option>
                                    </Select>
                                    // <Input disabled  style={{ width: '85%' }} />
                                )}
                            </FormItem>
                        )}
                        <Row style={{ marginTop: '40px' }}>
                            <Col className="form-title-line" {...formItemLayout.labelCol}>
                                <span className='form-title title-border-l-blue'>安全与限制策略</span>
                            </Col>
                        </Row>
                        <FormItem
                            {...formItemLayout}
                            label="调用次数限制"
                        >
                            {getFieldDecorator('callLimit', {
                                rules: [
                                    { required: true, message: '请输入调用次数限制' },
                                    {
                                        validator: function (rule: any, value: any, callback: any) {
                                            if (value && (value > 2000 || value < 1)) {
                                                const error = '请输入不大于2000的正整数'
                                                callback(error)
                                                return;
                                            }
                                            callback();
                                        }
                                    }
                                ],
                                initialValue: this.props.callLimit
                            })(
                                <Input style={{ width: '85%' }} type="number" placeholder="单用户每秒最大调用次数不超过2000次" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="安全组"
                        >
                            {getFieldDecorator('securityGroupIds', {
                                initialValue: this.props.securityGroupIds || []
                            })(
                                <Select style={{ width: '85%' }} mode="multiple" optionFilterProp="children">
                                    {this.getSecurityListView()}
                                </Select>
                                // <Input disabled  style={{ width: '85%' }} />
                            )}
                            <span style={{ marginLeft: '5px' }}>
                                <HelpDoc doc="securityTip" />
                            </span>
                        </FormItem>
                    </Form>
                </div>
                <div
                    className="steps-action"
                >
                    <Button loading={loading} onClick={() => this.cancelAndSave()}>
                        保存并退出
                    </Button>
                    <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.pass()}>下一步</Button>

                </div>
                <NewGroupModal groupChange={this.groupChange.bind(this)} visible={newGroupModalShow} cancel={this.hideNewGroup.bind(this)} />
            </div>
        )
    }
}
const wrappedComponent = Form.create<any>()(ManageBasicProperties);
export default wrappedComponent
