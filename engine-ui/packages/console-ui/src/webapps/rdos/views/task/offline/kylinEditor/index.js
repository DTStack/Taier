import React from 'react';
import { Form, Select, DatePicker, Row, Col, Input } from 'antd';
import { connect } from 'react-redux';
import moment from 'moment';

import { formItemLayout, DATA_SOURCE, DATA_SOURCE_TEXT } from '../../../../comm/const';
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
            isShowTimeRange: false
        }
    }

    componentDidMount () {
        const { currentTabData, setTabId, getDataSource } = this.props;
        setTabId(currentTabData.id);
        getDataSource();
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
        const { form } = this.props;
        setTimeout(() => {
            this.getCubeList(val);
        }, 0);
        // 重置表单
        form.resetFields();
        form.setFieldsValue({
            exeArgs: JSON.stringify({
                sourceId: val
            })
        });
    }
    // 更新CubeList数据
    getCubeList = (sourceId) => {
        const { currentTabData } = this.props;
        ajax.getOfflineCubeKylinInfo({
            sourceId
        }).then(res => {
            if (res.code === 1) {
                this.setState({
                    cubeListMap: res.data.cubeListMap || []
                }, () => {
                    if (this.state.cubeListMap) {
                        // 如果存在cubeName， 通过cubeName来更新state是否显示时间范围参数
                        const cubeName = currentTabData.exeArgs ? JSON.parse(currentTabData.exeArgs).cubeName : '';
                        if (!cubeName) {
                            this.setState({
                                isShowTimeRange: false
                            });
                            return;
                        }
                        cubeName && this.getIsPatition(cubeName);
                    }
                });
            }
        })
    }

    getIsPatition = (cubeName = '') => {
        const currentCubeNameData = this.state.cubeListMap.filter(item => item.name === cubeName)[0];
        this.setState({
            isShowTimeRange: currentCubeNameData && currentCubeNameData.isPatition === 1
        })
    }

    handleActionChange = (val) => {
        const { currentTabData, form } = this.props;
        // 每次Action改变时都重置时间
        form.resetFields(['range-time-picker']);
        form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...JSON.parse(currentTabData.exeArgs),
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
        const { currentTabData } = this.props;
        const { cubeListMap } = this.state;
        const formItems = [];
        // 数据处理  根据currentName处理当前table里面的数据
        const cubeListFltData = cubeListMap.filter(table => table.name === JSON.parse(currentTabData.exeArgs).cubeName)[0] || {};
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
        const { form, currentTabData } = this.props;
        form.resetFields(['range-time-picker']);
        this.getIsPatition(val);
        form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...JSON.parse(currentTabData.exeArgs),
                cubeName: val,
                startTime: '',
                endTime: '',
                // 改变cubeName即设置为true， 默认使用系统参数，
                isUseSystemVar: true
            }),
            systemVar: ''
        });
    }

    handleTimeChange = (dates, dateStrings) => {
        const { form, currentTabData } = this.props;
        form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...JSON.parse(currentTabData.exeArgs),
                startTime: dateStrings[0],
                endTime: dateStrings[1]
            })
        });
    }

    handleSystemVarChange = (e) => {
        const { form, currentTabData } = this.props;
        form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...JSON.parse(currentTabData.exeArgs)
            }),
            systemVar: e.target.value
        });
    }

    handleChangeMode = () => {
        const { form, currentTabData } = this.props;
        const exeArgsToJson = JSON.parse(currentTabData.exeArgs);
        form.resetFields(['range-time-picker']);
        form.setFieldsValue({
            exeArgs: JSON.stringify({
                ...exeArgsToJson,
                isUseSystemVar: !({ ...exeArgsToJson }.isUseSystemVar),
                startTime: '',
                endTime: ''
            }),
            systemVar: ''
        });
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { currentTabData } = this.props;
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
                >
                    <Row>
                        <Col>
                            Build
                        </Col>
                    </Row>
                </FormItem>
                {
                    (this.state.isShowTimeRange && exeArgsToJson.isUseSystemVar !== undefined) ? (
                        <FormItem
                            {...formItemLayout}
                            label="时间范围"
                        >
                            Start Date (Include)：
                            {getFieldDecorator('range-time-picker', {
                                rules: [{ type: 'array', required: true, message: '请选择时间范围!' }],
                                initialValue: exeArgsToJson.startTime && exeArgsToJson.endTime ? [moment(exeArgsToJson.startTime, dateFormat), moment(exeArgsToJson.endTime, dateFormat)] : null
                            })(
                                <RangePicker
                                    // 限制不能做隐式类型转换， isUseSystemVar只能为true or false
                                    style={{ 'display': exeArgsToJson.isUseSystemVar ? 'none' : 'inline-block', 'width': 'calc(100% - 210PX)' }}
                                    onChange={ this.handleTimeChange } showTime format="YYYY-MM-DD HH:mm:ss" />
                            )}
                            {
                                exeArgsToJson.isUseSystemVar ? (
                                    <Input style={{ 'width': 'calc(100% - 210PX)' }} placeholder='${}'
                                        onChange={ this.handleSystemVarChange }
                                        value={ currentTabData.systemVar }
                                    />
                                ) : null
                            }
                            <a onClick={this.handleChangeMode} style={{ marginLeft: '10PX' }}>
                                { exeArgsToJson.isUseSystemVar ? '自定义时间范围' : '使用系统变量' }
                            </a>
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
            values.taskVariables = matchTaskParams(taskCustomParams, values.systemVar);
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
