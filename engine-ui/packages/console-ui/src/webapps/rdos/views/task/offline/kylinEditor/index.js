import React from 'react';
import { Form, Select, DatePicker, Row, Col } from 'antd';
import { connect } from 'react-redux';
import moment from 'moment';

import { formItemLayout, DATA_SOURCE, DATA_SOURCE_TEXT, KYLIN_ACTION } from '../../../../comm/const';
import {
    dataSyncAction, workbenchAction
} from '../../../../store/modules/offlineTask/actionType';
import {
    dataSourceListAction
} from '../../../../store/modules/dataSource/actionTypes';
import ajax from '../../../../api';
import {
    matchTaskParams
} from '../../../../comm';

const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker;
const Option = Select.Option;

class KylinEditor extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            cubeListMap: [
            ],
            actionListMap: {
            }
        }
    }

    componentDidMount () {
        const { currentTabData } = this.props;
        this.props.setTabId(currentTabData.id);
        this.props.getDataSource();
        // 是否存在sourceId，获取本地state数据
        const sourceId = currentTabData.exeArgs ? JSON.parse(currentTabData.exeArgs).sourceId : '';
        sourceId && this.getCubeList(sourceId);
    }

    filterDataSourceList = () => {
        const { dataSourceList } = this.props;
        return dataSourceList.filter(item => {
            return item.type === DATA_SOURCE.KYLIN;
        });
    }

    handleSourceChange = (val) => {
        setTimeout(() => {
            this.getCubeList(val);
        }, 0);
        this.props.form.resetFields();
        this.props.form.setFieldsValue({
            exeArgs: JSON.stringify({
                // 重置表单
                // ...JSON.parse(this.props.currentTabData.exeArgs),
                sourceId: val
            })
        });
    }

    // 更新CubeList数据
    getCubeList = (sourceId) => {
        ajax.getOfflineCubeKylinInfo({
            sourceId
        }).then(res => {
            if (res.code === 1) {
                this.setState({
                    cubeListMap: res.data.cubeListMap || [],
                    actionListMap: res.data.actionListMap || {}
                })
            }
        })
    }

    handleActionChange = (val) => {
        // 每次Action改变时都重置时间
        this.props.form.resetFields(['range-time-picker']);
        this.props.form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...JSON.parse(this.props.currentTabData.exeArgs),
                buildType: val,
                startTime: '',
                endTime: ''
            })
        });
    }

    // 拆分数组
    group = (array, subGroupLength) => {
        let index = 0;
        let newArray = [];
        while (index < array.length) {
            newArray.push(array.slice(index, index += subGroupLength));
        }
        return newArray;
    }

    renderDynamicForm = () => {
        const { cubeListMap } = this.state;
        const formItems = [];
        // 数据处理  根据currentName处理当前table里面的数据
        const cubeListFltData = cubeListMap.filter(table => table.name === JSON.parse(this.props.currentTabData.exeArgs).cubeName)[0] || {};
        cubeListFltData.factTable && formItems.push(
            <FormItem
                {...formItemLayout}
                className='noMarginBottom'
                label="Fact Table"
                key='Fact Table'
            >
                <Row>
                    <Col
                    >
                        { cubeListFltData.factTable }
                    </Col>
                </Row>
            </FormItem>
        );
        cubeListFltData.lookups && formItems.push(
            <FormItem
                {...formItemLayout}
                label="LookUp Table"
                key='LookUp Table'
            >
                {
                    this.group(cubeListFltData.lookups, 3).map((src, idx) => {
                        return (
                            <Row key={idx} >
                                <Col>
                                    {
                                        src.map(item => item.table).join('  /  ')
                                    }
                                </Col>
                            </Row>
                        )
                    })
                }
            </FormItem>
        );
        return formItems;
    }

    handleCubeChange = (val) => {
        this.props.form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...JSON.parse(this.props.currentTabData.exeArgs),
                cubeName: val
            })
        });
    }

    handleTimeChange = (dates, dateStrings) => {
        this.props.form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...JSON.parse(this.props.currentTabData.exeArgs),
                startTime: dateStrings[0],
                endTime: dateStrings[1]
            })
        });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { currentTabData } = this.props;
        const { actionListMap } = this.state;
        // 默认exeArgs为"", 需要"{}"
        const exeArgsToJson = currentTabData.exeArgs ? JSON.parse(currentTabData.exeArgs) : '{}';
        const dateFormat = 'YYYY-MM-DD HH:mm:ss';
        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="数据源"
                    hasFeedback
                >
                    {getFieldDecorator('sourceId', {
                        rules: [{
                            required: true, message: '数据源为必填项'
                        }],
                        initialValue: exeArgsToJson.sourceId || ''
                    })(
                        <Select
                            onChange={this.handleSourceChange}>
                            {
                                this.filterDataSourceList().map(
                                    src => {
                                        let title = `${src.dataName}(${DATA_SOURCE_TEXT[src.type]})`;
                                        return (
                                            <Option
                                                key={src.id}
                                                dataType={src.type}
                                                name={src.dataName}
                                                value={`${src.id}`}
                                            >
                                                { title }
                                            </Option>
                                        )
                                    }
                                )
                            }
                        </Select>
                    )}
                </FormItem>
                <FormItem {...formItemLayout}
                    label="Cube"
                    hasFeedback
                >
                    {getFieldDecorator('cubeName', {
                        rules: [
                            {
                                required: true,
                                message: 'Cube为必填项'
                            }
                        ],
                        initialValue: exeArgsToJson.cubeName || ''
                    })(
                        <Select
                            showSearch
                            onSelect={this.handleCubeChange}
                            optionFilterProp="name"
                        >
                            {(this.state.cubeListMap || []).map(table => {
                                return (
                                    <Option
                                        key={`cube-${table.name}`}
                                        value={table.name}
                                    >
                                        {table.name}
                                    </Option>
                                );
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="Action"
                    hasFeedback
                >
                    {getFieldDecorator('buildType', {
                        rules: [{
                            required: true, message: 'Action不能为空'
                        }],
                        initialValue: exeArgsToJson.buildType || ''
                    })(
                        <Select
                            onChange={this.handleActionChange}>
                            {
                                Object.values(actionListMap).map(val => (
                                    <Option
                                        key={val}
                                    >
                                        {val}
                                    </Option>
                                ))
                            }
                        </Select>
                    )}
                </FormItem>
                {
                    (exeArgsToJson.buildType && exeArgsToJson.buildType !== KYLIN_ACTION.BUILD) ? (
                        <FormItem
                            {...formItemLayout}
                            label="时间范围"
                        >
                            {getFieldDecorator('range-time-picker', {
                                rules: [{ type: 'array', required: true, message: '请选择时间范围!' }],
                                initialValue: exeArgsToJson.startTime && exeArgsToJson.endTime ? [moment(exeArgsToJson.startTime, dateFormat), moment(exeArgsToJson.endTime, dateFormat)] : null
                            })(
                                <RangePicker
                                    onChange={ this.handleTimeChange } style={{ 'width': '100%' }} showTime format="YYYY-MM-DD HH:mm:ss" />
                            )}
                        </FormItem>
                    ) : null
                }
                {
                    this.renderDynamicForm()
                }
            </Form>
        )
    }
}

function validValues (values, props) {
    // invalid为一个验证标记，
    // 次标记为上方任务保存按钮是否有效提供依据
    if (values.hasOwnProperty('mainClass') && values.mainClass === '') { // mainClass不可为空
        return true;
    }
    return false;
}

const KylinEditorFormWrapper = Form.create({
    onValuesChange (props, values) {
        const { setFieldsValue, taskCustomParams } = props;
        // 获取任务自定义的参数
        if (values.hasOwnProperty('exeArgs')) {
            values.taskVariables = matchTaskParams(taskCustomParams, values.exeArgs);
        }
        values.invalid = validValues(values, props);
        setFieldsValue(values);
    }
})(KylinEditor);

class KylinTaskEditor extends React.Component {
    constructor (props) {
        super(props);
    }

    render () {
        return (<div className="m-taskedit" style={{ padding: 60 }}>
            <KylinEditorFormWrapper {...this.props} />
        </div>)
    }
}

const mapState = (state, ownProps) => {
    const { workbench } = state.offlineTask;
    const { currentTab, tabs } = workbench;
    const currentTabData = tabs.filter(tab => {
        return tab.id === currentTab;
    })[0];
    return {
        currentTabData,
        user: state.user,
        project: state.project,
        dataSourceList: state.dataSource.dataSourceList,
        taskCustomParams: workbench.taskCustomParams
    }
};

const mapDispatch = (dispatch) => {
    return {
        setTabId: (id) => {
            dispatch({
                type: dataSyncAction.SET_TABID,
                payload: id
            });
        },
        getDataSource: () => {
            ajax.getOfflineDataSource()
                .then(res => {
                    let data = []
                    if (res.code === 1) {
                        data = res.data
                    }
                    dispatch({
                        type: dataSourceListAction.LOAD_DATASOURCE,
                        payload: data
                    });
                });
        },
        setFieldsValue: (params) => {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    }
};

export default connect(mapState, mapDispatch)(KylinTaskEditor);
