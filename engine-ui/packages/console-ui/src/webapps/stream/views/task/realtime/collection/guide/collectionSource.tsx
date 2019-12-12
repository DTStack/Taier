import * as React from 'react';
import moment from 'moment';
import { get } from 'lodash';

import { Form, Select, Radio, Checkbox, DatePicker, Input, Button, Icon, Tag, message } from 'antd';

import { formItemLayout, DATA_SOURCE, CAT_TYPE, collectType } from '../../../../../comm/const'
import HelpDoc from '../../../../helpDoc';
import { isKafka } from '../../../../../comm';

import ajax from '../../../../../api/index';

import DataPreviewModal from '../../dataPreviewModal';
import MultipleTableSelect from './multipleTableSelect';
import EditMultipleTableModal from './editMultipleTableModal';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const CheckboxGroup = Checkbox.Group;

class CollectionSource extends React.Component<any, any> {
    _form: any;
    constructor (props: any) {
        super(props);
        this.state = {
            tableList: [],
            binLogList: [],
            dataSourceTypes: []
        }
    }
    componentDidMount () {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        this.getSupportDaTypes();
        if (sourceMap.sourceId) {
            if (sourceMap.type === DATA_SOURCE.MYSQL || sourceMap.type === DATA_SOURCE.POLAR_DB) {
                this.getValidMysqlTableList(sourceMap.sourceId);
            }
            if (sourceMap.collectType == collectType.FILE) {
                this.getBinLogList(sourceMap.sourceId);
            }
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const { collectionData } = nextProps;
        const { sourceMap = {} } = collectionData;
        const { collectionData: oldCol } = this.props;
        const { sourceMap: oldSource = {} } = oldCol;
        if (sourceMap.sourceId && oldSource.sourceId != sourceMap.sourceId) {
            if ((sourceMap.type === DATA_SOURCE.MYSQL || sourceMap.type === DATA_SOURCE.POLAR_DB) && sourceMap.sourceId) {
                this.setState({
                    tableList: []
                });
                this.getValidMysqlTableList(sourceMap.sourceId);
            }
        }
        /**
         * 当collectType是File，并且此时collectType发生过改变或者tab的id发生了改变，则去请求binlog列表
         */
        if (
            (sourceMap.collectType != oldSource.collectType || collectionData.id != oldCol.id) &&
            sourceMap.collectType == collectType.FILE) {
            if (sourceMap.sourceId) {
                this.getBinLogList(sourceMap.sourceId);
            }
        }
    }

    getSupportDaTypes () {
        ajax.getSupportDaTypes().then(
            (res: any) => {
                if (res.code == 1) {
                    this.setState({
                        dataSourceTypes: res.data
                    })
                }
            }
        )
    }

    async getValidMysqlTableList (sourceId: any) {
        let res = await ajax.checkSourceIsValid({
            sourceId
        });
        if (res && res.code == 1) {
            this.getTableList(sourceId);
        }
    }

    getTableList (sourceId: any) {
        ajax.getStreamTablelist({
            sourceId,
            isSys: false
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tableList: res.data || []
                });
            }
        });
    }
    getBinLogList (sourceId: any) {
        ajax.getBinlogListBySource({ sourceId })
            .then((res: any) => {
                if (res.code == 1) {
                    this.setState({
                        binLogList: res.data
                    })
                }
            })
    }
    clearBinLog () {
        this.setState({
            binLogList: []
        })
    }
    onFormValuesChange = () => {
        const { updateCurrentPage } = this.props;
        setTimeout(() => {
            this._form.validateFields(null, {}, (err: any, values: any) => {
                let invalidSubmit = false;
                if (err) {
                    invalidSubmit = true;
                }
                updateCurrentPage({
                    invalidSubmit
                });
            });
        }, 200);
    }
    checkGroup () {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        const { distributeTable = [], multipleTable } = sourceMap;
        if (multipleTable) {
            if (!distributeTable.length) {
                message.warn('分表模式下至少需要一个分组！');
                return false;
            }
            let nameMap: any = {};
            for (let i = 0; i < distributeTable.length; i++) {
                let table = distributeTable[i];
                if (!table.name) {
                    message.warn('请填写分组名！');
                    return false;
                }
                if (!/^\w*$/.test(table.name)) {
                    message.warn('分组名只能由字母、数字和下划线组成！');
                    return false;
                }
                if (nameMap[table.name]) {
                    message.warn('分组名不允许重复！');
                    return false;
                }
                nameMap[name] = true;
            }
        }
        return true;
    }
    next () {
        this._form.validateFields(null, {}, (err: any, values: any) => {
            if (!err) {
                if (this.checkGroup()) {
                    this.props.navtoStep(1);
                }
            }
        })
    }
    render () {
        const { tableList, binLogList, dataSourceTypes } = this.state;
        return (
            <div>
                <WrapCollectionSourceForm
                    ref={(f: any) => { this._form = f }}
                    dataSourceTypes={dataSourceTypes}
                    binLogList={binLogList}
                    tableList={tableList}
                    onFormValuesChange={this.onFormValuesChange}
                    {...this.props}
                />
                {!this.props.readonly && (
                    <div className="steps-action">
                        <Button type="primary" onClick={() => this.next()}>下一步</Button>
                    </div>
                )}
            </div>
        )
    }
}

class CollectionSourceForm extends React.Component<any, any> {
    _editMultipleTableModalKey: any;
    state: any = {
        sourceList: [], // TODO 此处 sourceList 跟 MySQL 的并未共用
        topicList: [],
        previewParams: {},
        previewVisible: false,
        editMultipleTableModalVisible: false,
        multipleTableDataIndex: null
    }

    componentDidMount () {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        if (sourceMap.type) {
            this.loadKafkaSourceList(collectionData.sourceMap.type);
        }
    }

    loadPreview = () => {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        this.setState({
            previewParams: {
                sourceId: sourceMap.sourceId,
                topic: sourceMap.topic
            },
            previewVisible: true
        })
    }

    onSourceChange = (sourceId: any) => {
        this.getTopicType(sourceId);
    }

    onSourceTypeChange = (sourceType: any) => {
        this.loadKafkaSourceList(sourceType);
    }

    loadKafkaSourceList = (sourceType: any) => {
        if (isKafka(sourceType)) {
            ajax.getTypeOriginData({ type: sourceType }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        sourceList: res.data
                    })
                }
            });
        }
    }
    onCompleteMultipleTableSelect = (selectKeys: any) => {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        const { distributeTable = [] } = sourceMap;
        this.props.updateSourceMap({
            distributeTable: [{ name: null, tables: selectKeys }, ...distributeTable]
        })
    }
    getTopicType (sourceId: any) {
        ajax.getTopicType({
            sourceId
        }).then((res: any) => {
            if (res.data) {
                this.setState({
                    topicList: res.data
                })
            }
        })
    }
    editMultipleTable (index: any) {
        this._editMultipleTableModalKey = Math.random();
        this.setState({
            editMultipleTableModalVisible: true,
            multipleTableDataIndex: index
        })
    }
    deleteGroup (index: any) {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        const { distributeTable = [] } = sourceMap;
        let newDistributeTable: any = [...distributeTable];
        newDistributeTable.splice(index, 1);
        this.props.updateSourceMap({
            distributeTable: newDistributeTable
        });
    }
    changeMultipleGroupName (index: any, e: any) {
        const value = e.target.value;
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        const { distributeTable = [] } = sourceMap;
        let newDistributeTable: any = [...distributeTable];
        newDistributeTable.splice(index, 1, {
            ...distributeTable[index],
            name: value
        });
        this.props.updateSourceMap({
            distributeTable: newDistributeTable
        });
    }
    changeMultipleTable (index: any, keys: any) {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        const { distributeTable = [] } = sourceMap;
        let newDistributeTable: any = [...distributeTable];
        newDistributeTable.splice(index, 1, {
            ...distributeTable[index],
            tables: keys
        })
        this.props.updateSourceMap({
            distributeTable: newDistributeTable
        });
        this.setState({
            editMultipleTableModalVisible: false,
            multipleTableDataIndex: null
        })
    }
    renderByCatType () {
        const { collectionData, form, binLogList } = this.props;
        const { getFieldDecorator } = form;
        const { sourceMap = {}, isEdit } = collectionData;
        const collectTypeValue = sourceMap.collectType
        switch (collectTypeValue) {
            case collectType.ALL: {
                return null
            }
            case collectType.TIME: {
                return <FormItem
                    {...formItemLayout}
                    label="起始时间"
                    style={{ textAlign: 'left' }}
                >
                    {getFieldDecorator('timestamp', {
                        rules: [{
                            required: true, message: '请选择起始时间'
                        }]
                    })(
                        <DatePicker
                            disabled={isEdit}
                            showTime
                            placeholder="请选择起始时间"
                            format="YYYY-MM-DD HH:mm:ss"
                        />
                    )}
                </FormItem>
            }
            case collectType.FILE: {
                return <FormItem
                    {...formItemLayout}
                    label="起始文件"
                >
                    {getFieldDecorator('journalName', {
                        rules: [{
                            required: true, message: '请填写起始文件'
                        }]
                    })(
                        <Select
                            placeholder="请填写起始文件"
                            disabled={isEdit}
                        >
                            {binLogList.map((binlog: any) => {
                                return <Option key={binlog}>{binlog}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
            }
        }
    }

    renderForm () {
        const { editMultipleTableModalVisible, multipleTableDataIndex } = this.state;
        let { collectionData, tableList } = this.props;
        let { dataSourceList = [], sourceMap, isEdit } = collectionData;
        if (!sourceMap) return [];
        const { getFieldDecorator } = this.props.form;
        const allTable = sourceMap.allTable;
        const { type, sourceId, multipleTable, distributeTable = [] } = sourceMap;
        const multipleTableData = distributeTable[multipleTableDataIndex];
        const isCollectTypeEdit = !!sourceId;

        switch (type) {
            case DATA_SOURCE.POLAR_DB:
            case DATA_SOURCE.MYSQL: {
                return [
                    <FormItem
                        key="sourceId"
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{ required: true, message: '请选择数据源' }]
                        })(
                            <Select
                                disabled={isEdit}
                                placeholder="请选择数据源"
                                style={{ width: '100%' }}
                            >
                                {dataSourceList.map((item: any) => {
                                    if (item.type != type) {
                                        return null
                                    }
                                    return <Option key={item.id} value={item.id}>{item.dataName}</Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        key="multipleTable"
                        {...formItemLayout}
                        label="是否分表"
                    >
                        {getFieldDecorator('multipleTable', {
                            valuePropName: 'checked'
                        })(
                            <Checkbox>分表</Checkbox>
                        )}
                        <HelpDoc style={{ right: 'auto' }} doc="multipleTable" />
                    </FormItem>,
                    multipleTable && (
                        <React.Fragment>
                            <FormItem
                                key="onlyShowTable"
                                {...formItemLayout}
                                label="表"
                            >
                                <MultipleTableSelect tableList={tableList} onComplete={this.onCompleteMultipleTableSelect} />
                            </FormItem>
                            {distributeTable && distributeTable.map((table: any, index: any) => {
                                const couldEdit = !(isEdit && table.isSaved);
                                return (
                                    <FormItem
                                        key={`${index}`}
                                        {...formItemLayout}
                                        label="分组"
                                        required
                                    >
                                        <Input onChange={this.changeMultipleGroupName.bind(this, index)} value={table.name} disabled={!couldEdit} placeholder='请输入分组名，将参与写入目标名称的拼接，例如：写入Hive表时，分组名将作为Hive表名的一部分' />
                                        <div style={{ maxHeight: 200, overflow: 'auto' }}>
                                            <span style={{ marginTop: '5px', padding: '0px 18px 0px 8px' }}>
                                                <Button onClick={this.editMultipleTable.bind(this, index)} size="small" type="dashed"> + 编辑</Button>
                                            </span>
                                            {(table.tables || []).map((tableName: any) => {
                                                return <span style={{ marginTop: '5px', paddingRight: '8px' }} key={tableName} ><Tag>{tableName}</Tag></span>
                                            })}
                                        </div>
                                        {couldEdit && (<a onClick={this.deleteGroup.bind(this, index)} style={{ position: 'absolute', right: '-30px', top: '0px' }}>删除</a>)}
                                    </FormItem>
                                )
                            })}
                            <EditMultipleTableModal
                                key={this._editMultipleTableModalKey}
                                visible={editMultipleTableModalVisible}
                                tableList={tableList}
                                selectKeys={get(multipleTableData || {}, 'tables')}
                                onCancel={() => {
                                    this.setState({
                                        editMultipleTableModalVisible: false,
                                        multipleTableData: null
                                    })
                                }}
                                onOk={(keys: any) => {
                                    this.changeMultipleTable(multipleTableDataIndex, keys);
                                }}
                            />
                        </React.Fragment>
                    ),
                    !multipleTable && (
                        <FormItem
                            key="table"
                            {...formItemLayout}
                            label="表"
                        >
                            {getFieldDecorator('table', {
                                rules: [{
                                    required: true, message: '请选择表'
                                }]
                            })(
                                <Select
                                    mode="multiple"
                                    style={{ width: '100%' }}
                                    placeholder="请选择表"

                                >
                                    {tableList.length ? [<Option key={-1} value={-1}>全部</Option>].concat(tableList.map(
                                        (table: any) => {
                                            return <Option disabled={allTable} key={`${table}`} value={table}>
                                                {table}
                                            </Option>
                                        }
                                    )) : [<Option key={-1} value={-1}>全部</Option>]}
                                </Select>
                            )}
                        </FormItem>
                    ),
                    <FormItem
                        key="collectType"
                        {...formItemLayout}
                        label="采集起点"
                        style={{ textAlign: 'left' }}
                    >
                        {getFieldDecorator('collectType', {
                            rules: [{
                                required: true, message: '请选择采集起点'
                            }]
                        })(
                            <RadioGroup disabled={isEdit || !isCollectTypeEdit}>
                                <Radio value={collectType.ALL}>从任务运行时开始</Radio>
                                <Radio value={collectType.TIME}>按时间选择</Radio>
                                <Radio value={collectType.FILE}>按文件选择</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                    this.renderByCatType(),
                    <FormItem
                        key="cat"
                        {...formItemLayout}
                        label="数据操作"
                    >
                        {getFieldDecorator('cat', {
                            rules: [{
                                required: true, message: '请选择数据操作'
                            }]
                        })(
                            <CheckboxGroup options={
                                [
                                    { label: 'Insert', value: CAT_TYPE.INSERT },
                                    { label: 'Update', value: CAT_TYPE.UPDATE },
                                    { label: 'Delete', value: CAT_TYPE.DELETE }
                                ]
                            }
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        key="pavingData"
                        {...formItemLayout}
                        label="格式转换"
                    >
                        {getFieldDecorator('pavingData', {
                            valuePropName: 'checked'
                        })(
                            <Checkbox disabled={isEdit}>嵌套JSON平铺</Checkbox>
                        )}
                        <HelpDoc style={{ right: 'auto' }} doc="sourceFormat" />
                    </FormItem>
                ]
            }
            case DATA_SOURCE.BEATS: {
                return [
                    <FormItem
                        key="macAndIp"
                        {...formItemLayout}
                        label="主机名/IP"
                    >
                        {getFieldDecorator('macAndIp', {})(
                            <Input disabled />
                        )}
                    </FormItem>,
                    <FormItem
                        key="port"
                        {...formItemLayout}
                        label="端口"
                    >
                        {getFieldDecorator('port', {
                            rules: [{
                                validator: (rule: any, value: any, callback: any) => {
                                    if (value) {
                                        if (parseInt(value)) {
                                            callback()
                                        } else {
                                            const error = '请输入正确的端口'
                                            callback(error)
                                        }
                                    } else {
                                        callback()
                                    }
                                }
                            }]
                        })(
                            <Input
                                // disabled={isEdit}
                                placeholder="请输入端口"
                                style={{ width: '100%' }}
                            />
                        )}
                        <HelpDoc doc="binlogPortHelp" />
                    </FormItem>
                ]
            }
            case DATA_SOURCE.KAFKA:
            case DATA_SOURCE.KAFKA_11:
            case DATA_SOURCE.KAFKA_09:
            case DATA_SOURCE.KAFKA_10: {
                const { topicList, sourceList } = this.state;
                const sourceOptions = sourceList.map((o: any) => {
                    return <Option key={o.id} value={o.id}>{o.name}</Option>
                })
                return [
                    <FormItem
                        {...formItemLayout}
                        key="sourceId"
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [
                                { required: true, message: '请选择数据源' }
                            ]
                        })(
                            <Select
                                disabled={isEdit && type === DATA_SOURCE.KAFKA}
                                showSearch
                                placeholder="请选择数据源"
                                className="right-select"
                                onChange={this.onSourceChange}
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {
                                    sourceOptions
                                }
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        key="topic"
                        {...formItemLayout}
                        label="Topic"
                    >
                        {getFieldDecorator('topic', {
                            rules: [{
                                required: true, message: '请选择topic'
                            }]
                        })(
                            <Select
                                disabled={isEdit}
                                style={{ width: '100%' }}
                                placeholder="请选择topic"
                            >
                                {topicList.map(
                                    (topic: any) => {
                                        return <Option key={`${topic}`} value={topic}>
                                            {topic}
                                        </Option>
                                    }
                                )}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem key="preview">
                        <p className="txt-center">
                            <a
                                style={{ cursor: 'pointer' }}
                                href="javascript:void(0)"
                                onClick={this.loadPreview.bind(this)}
                            >
                                数据预览 <Icon type="down" />
                            </a>
                        </p>
                    </FormItem>
                ];
            }
            default: {
                return null;
            }
        }
    }

    render () {
        let { collectionData, dataSourceTypes = [] } = this.props;
        let { isEdit } = collectionData;
        const { getFieldDecorator } = this.props.form;
        return (
            <div>
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据源类型"
                    >
                        {getFieldDecorator('type', {
                            rules: [{ required: true, message: '请选择数据源类型' }]
                        })(
                            <Select
                                allowClear
                                disabled={isEdit}
                                onChange={this.onSourceTypeChange}
                                placeholder="请选择数据源类型"
                                style={{ width: '100%' }}
                            >
                                {dataSourceTypes.filter((item: any) => {
                                    return item.value != DATA_SOURCE.HIVE
                                }).map((item: any) => {
                                    return <Option key={item.value} value={item.value} >{item.key}</Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                    {this.renderForm()}
                </Form>
                <DataPreviewModal
                    visible={this.state.previewVisible}
                    onCancel={() => { this.setState({ previewVisible: false, previewParams: {} }) }}
                    params={this.state.previewParams}
                />
            </div>
        )
    }
}

const WrapCollectionSourceForm = Form.create({
    onValuesChange (props: any, fields: any) {
        let clear = false;
        /**
         * 数据源类型改变，清空数据源
         */
        if (fields.type != undefined) {
            fields.sourceId = undefined;
            clear = true;
        }
        /**
         * sourceId改变,则清空表
         */
        if (fields.hasOwnProperty('sourceId')) {
            clear = true
        }
        if (fields.hasOwnProperty('multipleTable')) {
            fields.table = [];
            fields.distributeTable = undefined;
            fields.allTable = false;
        }
        /**
         * moment=>时间戳,并且清除其他的选项
         */
        if (fields.timestamp) {
            fields.timestamp = fields.timestamp.valueOf()
            fields.journalName = null;
        }
        if (fields.journalName) {
            fields.timestamp = null;
        }
        if (fields.collectType != undefined && fields.collectType == collectType.ALL) {
            fields.journalName = null;
            fields.timestamp = null;
        }
        /**
         * 改变table的情况
         * 1.包含全部，则剔除所有其他选项，设置alltable=true
         * 2.不包含全部，设置alltable=false
         */
        if (fields.table) {
            if (fields.table.includes(-1)) {
                fields.table = [];
                fields.allTable = true;
            } else {
                fields.allTable = false;
            }
        }
        props.updateSourceMap(fields, clear);
        if (props.onFormValuesChange) {
            props.onFormValuesChange(props, fields);
        }
    },
    mapPropsToFields (props: any) {
        const { collectionData } = props;
        const sourceMap = collectionData.sourceMap;
        if (!sourceMap) return {};
        return {
            type: {
                value: sourceMap.type
            },
            port: {
                value: sourceMap.port
            },
            sourceId: {
                value: sourceMap.sourceId
            },
            topic: {
                value: sourceMap.topic
            },
            table: {
                value: sourceMap.allTable ? -1 : sourceMap.table
            },
            collectType: {
                value: sourceMap.collectType
            },
            cat: {
                value: sourceMap.cat
            },
            pavingData: {
                value: sourceMap.pavingData
            },
            timestamp: {
                value: sourceMap.timestamp ? moment(sourceMap.timestamp) : undefined
            },
            journalName: {
                value: sourceMap.journalName
            },
            macAndIp: {
                value: '任务运行时自动分配，无需手动指定'
            },
            multipleTable: {
                value: sourceMap.multipleTable
            }
        }
    }
})(CollectionSourceForm);

export default CollectionSource;
