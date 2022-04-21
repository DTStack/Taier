import React from 'react';
import { Form, Checkbox, InputNumber, Select, FormProps, FormInstance } from 'antd';
// import LabelTip from '@/components/labelTip';
import { debounce } from 'lodash';
import { DIRTY_DATA_SAVE } from '@/constant';
import stream from '@/api/stream';

const { Option } = Select;

const FormItem = Form.Item;

const MAX_ROWS = 1000000;
const MAX_FAILED_ROWS = 1000000;
const MAX_PRINT_SPEED = 1000000;

interface IProps extends FormProps {
    formItemLayout: any;
    data: any;
    isDirtyDataManage: boolean;
    onChange?: any;
    form: FormInstance
}
interface IState {
    sourceLists: any[];
    tableLists: any[];
}

export default class DirtyDataLog extends React.PureComponent<IProps, IState> {
    state: IState = {
        sourceLists: [],
        tableLists: []
    }

    componentDidMount() {
        const { outputType, linkInfo } = this.props.data;
        this.getSourceLists();
        if (outputType === DIRTY_DATA_SAVE.BY_MYSQL) {
            this.getTableList(linkInfo?.sourceId);
        }
    }

    // 脏数据保存方式变更
    handleDirtySaveChange = (value: string) => {
        this.handleChange(value, 'outputType');
        this.props.form.setFieldsValue({
            linkInfo: {
                sourceId: undefined,
                table: undefined
            }
        });
        this.setState({
            tableLists: []
        });
    }

    // 获取脏数据写入库列表
    getSourceLists = () => {
        stream.getStreamDataSourceList({
            pageSize: 500,
            currentPage: 1,
            name: '',
            groupTags: ['MySQL']
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    sourceLists: res?.data?.data || [],
                    tableLists: []
                })
            }
        });
    }

    // 库选择
    handleDirtyWriteDbChange = (value: number) => {
        const linkInfo = {
            sourceId: value,
            table: undefined
        }
        this.handleChange(linkInfo, 'linkInfo');
        this.props.form.setFieldsValue({
            linkInfo: {
                table: undefined
            }
        });
        this.getTableList(value);
    }

    // 获取脏数据写入表列表
    getTableList = (sourceId: number, searchKey?: string) => {
        if (!sourceId) {
            return;
        }
        stream.getStreamTablelist({
            sourceId,
            isSys: false,
            searchKey
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({ tableLists: res.data || [] });
            }
        });
    }

    handleChange = (value: any, key: string, subKey?: string) => {
        const { onChange, data } = this.props;
        const nValue = subKey
            ? Object.assign({}, data[key], { [subKey]: value })
            : value;
        let params = Object.assign({}, data, {
            [key]: nValue
        });
        if (key === 'isDirtyDataManage') {
            Object.assign(params, {
                maxRows: 100000,
                maxCollectFailedRows: 100000,
                outputType: DIRTY_DATA_SAVE.NO_SAVE,
                linkInfo: {
                    sourceId: undefined,
                    table: undefined
                },
                logPrintInterval: 0
            });
            // 不开启，重置原有数据
            if (!value) {
                params = { [key]: value }
            }
        }
        if (key === 'outputType') {
            Object.assign(params, {
                linkInfo: {
                    sourceId: undefined,
                    table: undefined
                }
            })
        }
        if (onChange) {
            onChange(params);
        }
    }

    // 数字输入框校验
    validateInputNumber = (value: any, callback: any, min: number, max: number) => {
        if (!/^\d+$/.test(value)) {
            let err = `请输入数字`;
            callback(err);
            return;
        }
        if (value > max) {
            let err = `最大值为${max}`;
            callback(err);
        } else if (value < min) {
            let err = `最小为${min}`;
            callback(err);
        } else {
            callback();
        }
    }

    // 打印频率校验
    validatePrintSpeed = (rule: any, value: number, callback: any) => {
        const { data } = this.props;
        const { maxRows } = data;
        if (maxRows < value) {
            let err = '日志打印频率不可超过脏数据最大值';
            callback(err);
            return;
        }
        this.validateInputNumber(value, callback, 0, MAX_PRINT_SPEED)
    }

    debounceTableNameSearch = debounce(this.getTableList, 500)

    render() {
        const { tableLists, sourceLists } = this.state;
        const { formItemLayout, form, data, isDirtyDataManage } = this.props;
        const { outputType } = data;

        return (
            <React.Fragment>
                <FormItem
                    label='脏数据记录'
                    tooltip='开启后，系统将记录脏数据；脏数据保存后您可以在“任务运维-任务详情-脏数据”中查看脏数据详情。'
                    name='isDirtyDataManage'
                    initialValue={isDirtyDataManage || false}
                    valuePropName='checked'
                >
                    <Checkbox onChange={(e: any) => this.handleChange(e.target.checked, 'isDirtyDataManage')}>开启</Checkbox>
                </FormItem>
                {isDirtyDataManage && (
                    <React.Fragment>
                        <FormItem
                            label='脏数据最大值'
                            tooltip='脏数据达到最大值时，任务自动失败'
                            name='maxRows'
                            initialValue={data?.maxRows || 100000}
                            rules={[{
                                validator: (rule: any, value: number, callback: any) => this.validateInputNumber(value, callback, 0, MAX_ROWS)
                            }]}
                        >
                            <InputNumber style={{ width: '93%' }} min={0} max={MAX_ROWS} addonAfter='条' onChange={(value: number) => this.handleChange(value, 'maxRows')} />
                        </FormItem>
                        <FormItem
                            label='失败条数'
                            tooltip='当脏数据处理失败次数超过设定值时，任务失败'
                            name='maxCollectFailedRows'
                            initialValue={data?.maxCollectFailedRows || 100000}
                            rules={[{
                                validator: (rule: any, value: number, callback: any) => this.validateInputNumber(value, callback, 0, MAX_FAILED_ROWS)
                            }]}
                        >
                            <InputNumber style={{ width: '93%' }} min={0} max={MAX_FAILED_ROWS} addonAfter='条' onChange={(value: number) => this.handleChange(value, 'maxCollectFailedRows')} />
                        </FormItem>
                        <FormItem
                            label='脏数据保存'
                            tooltip='仅当保存到数据库时，可展示脏数据分析内容'
                            name='outputType'
                            initialValue={data?.outputType || DIRTY_DATA_SAVE.NO_SAVE}
                        >
                            <Select onChange={this.handleDirtySaveChange}>
                                <Option value={DIRTY_DATA_SAVE.NO_SAVE}>不保存，仅日志输出</Option>
                                <Option value={DIRTY_DATA_SAVE.BY_MYSQL}>保存至MySQL</Option>
                            </Select>
                        </FormItem>
                        {outputType === DIRTY_DATA_SAVE.BY_MYSQL && (
                            <React.Fragment>
                                <FormItem
                                    label="脏数据写入库"
                                    name={['linkInfo', 'sourceId']}
                                    initialValue={data?.linkInfo?.sourceId || undefined}
                                    rules={[{ required: true, message: '请选择脏数据写入的MySQL库' }]}
                                >
                                    <Select
                                        placeholder="请选择脏数据写入的MySQL库"
                                        onChange={this.handleDirtyWriteDbChange}
                                    >
                                        {Array.isArray(sourceLists) && sourceLists.map((item: any) => (
                                            <Option key={item.id} value={item.id}>{item.dataName}</Option>
                                        ))}
                                    </Select>
                                </FormItem>
                                <FormItem
                                    label='脏数据写入表'
                                    tooltip={<div>
                                        <p>脏数据表会写入选择的MySQL库中, 表名默认系统分配&quot;dirty_任务名称&quot;</p>
                                        <p>同时，支持写入自定义表，用户自定义表名，数据写入时进行新建。</p>
                                    </div>}
                                    name={['linkInfo', 'table']}
                                    initialValue={data?.linkInfo?.table || undefined}
                                >
                                    <Select
                                        placeholder="请选择脏数据写入的MySQL表，为空则系统默认分配"
                                        showSearch
                                        allowClear
                                        onSearch={(value: string) => this.debounceTableNameSearch(data?.linkInfo?.sourceId, value)}
                                        onChange={(value: number) => this.handleChange(value, 'linkInfo', 'table')}
                                    >
                                        {Array.isArray(tableLists) && tableLists.map((item: any) => (
                                            <Option key={item} value={item}>{item}</Option>
                                        ))}
                                    </Select>
                                </FormItem>
                            </React.Fragment>
                        )}
                        <FormItem
                            label='日志打印频率'
                            tooltip='设定脏数据在日志中输出间隔，默认为0不打印；若开启脏数据保存，则脏数据直接保存至指定库，不再输出至日志'
                            name='logPrintInterval'
                            initialValue={data?.logPrintInterval || 0}
                            rules={[{ validator: this.validatePrintSpeed }]}
                        >
                            <InputNumber style={{ width: '89%' }} min={0} max={MAX_PRINT_SPEED} addonAfter='条/次' onChange={(value: number) => this.handleChange(value, 'logPrintInterval')} />
                        </FormItem>
                    </React.Fragment>
                )}
            </React.Fragment>
        )
    }
}
