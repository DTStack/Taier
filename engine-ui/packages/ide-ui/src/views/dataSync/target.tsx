import * as React from 'react'
import { connect } from 'react-redux'
import { Form, Input, Select, Button, Radio, Icon, message, Spin, AutoComplete } from 'antd'
import { isEmpty, debounce, get } from 'lodash'
import assign from 'object-assign'

import { Utils } from '@dtinsight/dt-utils'
import { singletonNotification, filterValueOption } from '../../components/func'
import { getProjectTableTypes } from '../../controller/dataSync/tableType'
import ajax from '../../api'
import {
    targetMapAction,
    dataSyncAction,
    settingAction,
    workbenchAction
} from '../../controller/dataSync/actionType'
import {
    formItemLayout,
    DATA_SOURCE,
    DATA_SOURCE_TEXT
} from '../../comm/const'

import { formJsonValidator } from '../../comm'

import HelpDoc from '../../components/helpDoc'

const FormItem = Form.Item
const Option = Select.Option
const RadioGroup = Radio.Group
const TextArea = Input.TextArea

class TargetForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props)

        this.state = {
            tableList: [],
            tableListSearch: [],
            visible: false,
            modalLoading: false,
            tablePartitionList: [], // 表分区列表
            loading: false, // 请求
            isImpalaHiveTable: false, // 是否是impala hive表
            schemaList: [],
            kingbaseId: '',
            tableListLoading: false,
            fetching: false,
            schemaId: '',
            version: '0',
            bucketList: [] // aws s3 bucket 数据
        }
    }

    componentDidMount () {
        const { targetMap, getProjectTableTypes, project } = this.props
        const { sourceId, type = {} } = targetMap
        const schema = isEmpty(targetMap)
            ? ''
            : targetMap?.schema ?? targetMap.type.schema
        const projectId = project && project.id
        if (projectId) {
            getProjectTableTypes(projectId)
        }
        if (!sourceId) {
            return
        }
        if (type.type === DATA_SOURCE.POSTGRESQL || type.type === DATA_SOURCE.ORACLE) {
            this.getSchemaList(sourceId)
            schema ? this.getTableList(sourceId, schema) : this.getTableList(sourceId)
        } else {
            this.getTableList(sourceId)
        }
        if (type) {
            this.checkIsNativeHive(type.tableName)
            // 如果是已经添加过分区信息，则加载其分区列表信息
            if (type.partition) {
                this.getHivePartitions(type.table)
            }
        }
    }

    getSchemaList = (sourceId: any) => {
        ajax.getAllSchemas({
            sourceId
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    schemaList: res.data || [],
                    kingbaseId: sourceId
                })
            }
        })
    }

    getDataSourceVersion = async (dataSourceId: number) => {
        const res = await ajax.getDataSourceVersion({ dataSourceId })
        if (res.code === 1) {
            this.setState({
                version: res.data
            })
        }
    }

    getTableList = (sourceId: any, schema?: any, name?: any) => {
        const ctx = this
        const { targetMap } = this.props
        // 排除条件
        if (targetMap.type?.type === DATA_SOURCE.HDFS) {
            return true
        }

        ctx.setState({
            tableList: [],
            tableListSearch: [],
            tableListLoading: !name,
            schemaId: schema,
            fetching: !!name
        }, () => {
            ajax.getOfflineTableList({
                sourceId,
                isSys: false,
                schema,
                name,
                isRead: false
            })
                .then((res: any) => {
                    if (res.code === 1) {
                        const { data = [] } = res
                        let arr = data
                        if (data.length && data.length > 200) {
                            arr = data.slice(0, 200)
                        }
                        ctx.setState({
                            tableList: res.data,
                            tableListSearch: arr
                        })
                    }
                })
                .finally(() => {
                    ctx.setState({ tableListLoading: false, fetching: false })
                })
        })
    }

    onSearchTable = (str: any) => {
        const { tableList } = this.state
        let arr = tableList.filter((item: any) => item.indexOf(str) !== -1)
        if (arr.length && arr.length > 200) {
            arr = arr.slice(0, 200)
        }
        this.setState({
            tableListSearch: arr
        })
    }

    /**
     * 根据数据源id获取数据源信息
     * @param {*} id
     */
    getDataObjById (id: any) {
        const { dataSourceList } = this.props
        return dataSourceList.filter((src: any) => {
            return `${src.id}` === id
        })[0]
    }

    changeSource (value: any, option: any) {
        const { handleSourceChange } = this.props
        const { dataType } = option.props
        setTimeout(() => {
            // 有schema才需要获取schemalist
            (dataType === DATA_SOURCE.POSTGRESQL ||
            dataType === DATA_SOURCE.ORACLE) &&
            this.getSchemaList(value)
        }, 0)
        handleSourceChange(this.getDataObjById(value))
        this.resetTable()
    }

    resetTable () {
        const { form } = this.props
        this.changeTable('')
        // 这边先隐藏结点，然后再reset，再显示。不然会有一个组件自带bug。
        this.setState({
            selectHack: true
        }, () => {
            form.resetFields(['table'])
            this.setState({
                selectHack: false
            })
        })
    }

    getHivePartitions = (tableName: any) => {
        const {
            targetMap, handleTargetMapChange
        } = this.props

        const { sourceId, type } = targetMap
        // TODO 这里获取 Hive 分区的条件有点模糊
        if (type && (
            type.type === DATA_SOURCE.HIVE_1 ||
            type.type === DATA_SOURCE.HIVE_2 ||
            type.type === DATA_SOURCE.HIVE_3 ||
            type.type === DATA_SOURCE.HIVE_SERVER
        )) {
            ajax.getHivePartitions({
                sourceId: sourceId,
                tableName
            }).then((res: any) => {
                this.setState({
                    tablePartitionList: res.data || []
                })
                const havePartition = res.data && res.data.length > 0
                handleTargetMapChange({ havePartition })
            })
        }
    }

    changeTable (type?: any, value?: any) {
        if (value) {
            // Reset partition
            this.props.form.setFieldsValue({ partition: '' })
            const schema = this.props.form.getFieldValue('schema')
            // 获取表列字段
            this.getTableColumn(value, schema)
            // 检测是否有 native hive
            this.checkIsNativeHive(value)
        }
        this.submitForm()
    }

    getPartitionType = async (tableName: string, isUpdate: boolean) => {
        const {
            targetMap
        } = this.props
        const res = await ajax.getPartitionType({ sourceId: targetMap.sourceId, tableName })
        if (res.code === 1) {
            const data = res.data || {}
            const isImpalaHiveTable = data.tableLocationType === 'hive'
            this.setState({ isImpalaHiveTable }, () => {
                this.getHivePartitions(tableName)
            })
            if (isUpdate) { // isUpdate为 true reset writeMode
                this.props.handleTargetMapChange({ writeMode: isImpalaHiveTable ? 'replace' : 'insert' })
            }
        }
    }

    getTableColumn = (tableName: any, schema?: any) => {
        const { form, handleTableColumnChange, targetMap } = this.props
        const sourceId = form.getFieldValue('sourceId')

        this.setState({
            loading: true
        })
        // Hive 作为结果表时，需要获取分区字段
        const targetType = get(targetMap, 'type.type', null)
        const includePart = +targetType === DATA_SOURCE.HIVE_1 || +targetType === DATA_SOURCE.HIVE_2 || +targetType === DATA_SOURCE.HIVE_3 || +targetType === DATA_SOURCE.HIVE_SERVER

        ajax.getOfflineTableColumn({
            sourceId,
            schema,
            tableName,
            isIncludePart: includePart
        }).then((res: any) => {
            this.setState({
                loading: false
            })
            if (res.code === 1) {
                handleTableColumnChange(res.data)
            } else {
                handleTableColumnChange([])
            }
        })
    }

    checkIsNativeHive (tableName: any) {
        const { form } = this.props
        const sourceId = form.getFieldValue('sourceId')
        if (!tableName || !sourceId) { return false }
    }

    submitForm = () => {
        const {
            form, updateTabAsUnSave, handleTargetMapChange
        } = this.props

        setTimeout(() => {
            /**
             * targetMap
             */
            let values = form.getFieldsValue()
            const keyAndValues = Object.entries(values)
            /**
             * 这边将 ·writeMode@hdfs· 类的key全部转化为writeMode
             * 加上@ 的原因是避免antd相同key引发的bug
             */
            values = (() => {
                const values: any = {}
                keyAndValues.forEach(([key, value]) => {
                    if (key.indexOf('@') > -1) {
                        values[key.split('@')[0]] = value
                    } else {
                        values[key] = value
                    }
                })
                return values
            })()
            // 去空格
            if (values.partition) {
                values.partition = Utils.removeAllSpaces(values.partition)
            }
            if (values.path) {
                values.path = Utils.removeAllSpaces(values.path)
            }
            if (values.fileName) {
                values.fileName = Utils.removeAllSpaces(values.fileName)
            }
            if (values.bucket) {
                values.bucket = Utils.removeAllSpaces(values.bucket)
            }
            if (values.object) {
                values.object = Utils.removeAllSpaces(values.object)
            }
            if (values.fileName) {
                values.fileName = Utils.removeAllSpaces(values.fileName)
            }
            if (values.ftpFileName) {
                values.ftpFileName = Utils.removeAllSpaces(values.ftpFileName)
            }
            const srcmap = assign(values, {
                src: this.getDataObjById(values.sourceId)
            })

            // 处理数据同步变量
            handleTargetMapChange(srcmap)
            updateTabAsUnSave()
        }, 0)
    }

    validateChineseCharacter = (data: any) => {
        const reg = /(，|。|；|[\u4e00-\u9fa5]+)/ // 中文字符，中文逗号，句号，分号
        let has = false
        const fieldsName: any = []
        if (data.path && reg.test(data.path)) {
            has = true
            fieldsName.push('路径')
        }
        if (data.fileName && reg.test(data.fileName)) {
            has = true
            fieldsName.push('文件名')
        }
        if (data.ftpFileName && reg.test(data.ftpFileName)) {
            has = true
            fieldsName.push('文件名')
        }
        if (data.fieldDelimiter && reg.test(data.fieldDelimiter)) {
            has = true
            fieldsName.push('列分隔符')
        }
        if (has) {
            singletonNotification('提示', `${fieldsName.join('、')}参数中有包含中文或者中文标点符号！`, 'warning')
        }
    }

    prev (cb: any) {
        /* eslint-disable-next-line */
        cb.call(null, 0);
    }

    onSearchObject = (str: any, sourceId: any) => {
        this.getBucketList(sourceId, str)
    }

    next (cb: any) {
        const { form, currentTabData, saveDataSyncToTab, dataSync } = this.props
        form.validateFields((err: any, values: any) => {
            if (!err) {
                saveDataSyncToTab({
                    id: currentTabData.id,
                    data: dataSync
                })
                this.validateChineseCharacter(values)
                /* eslint-disable-next-line */
                cb.call(null, 2);
            }
        })
    }

    handleCancel () {
        this.setState({
            textSql: '',
            visible: false
        })
    }

    createTable () {
        const { textSql } = this.state
        const { targetMap, form } = this.props
        const tableType = form.getFieldValue('tableType')
        const dataSourceType = targetMap.type && targetMap.type.type
        this.setState({
            modalLoading: true
        })
        ajax.createDdlTable({ sql: textSql, sourceId: targetMap.sourceId, tableType }).then((res: any) => {
            this.setState({
                modalLoading: false
            })
            if (res.code === 1) {
                this.getTableList(targetMap.sourceId)
                this.changeTable(dataSourceType, res.data.tableName)
                this.props.form.setFieldsValue({ table: res.data.tableName })
                this.setState({
                    visible: false
                })
                message.success('表创建成功!')
            }
        })
    }

    getTableData = (type: any, schema: any, value: any, sourceKey?: any) => {
        if (value) {
            this.setState({
                loading: true
            })

            this.getTableColumn(value, schema)
        }
        // 不可简化sourceKey, 在submitForm上对应的不同的逻辑，即第四个参数对应的逻辑不同，在不同场景可能不存在第四个参数，不能简化
        this.submitForm()
        this.setState({
            showPreview: false
        })
    }

    getBucketList = async (resourceId:number, schema?: any) => {
        const { currentTab } = this.props
        const params = {
            projectId: currentTab,
            sourceId: resourceId,
            schema
        }
        const res = await ajax.getAllSchemas(params)
        const { data } = res
        if (res.code === 1 && Array.isArray(res.data) && res.data?.length > 0) {
            this.setState({
                bucketList: data
            })
        }
    }

    checkEffective=() => {
        const { tableListSearch } = this.state
        const { form, targetMap } = this.props
        const value = form.getFieldValue('table')
        if (!Array.isArray(tableListSearch) || tableListSearch.length === 0 || !tableListSearch?.includes(value)) {
            form.setFieldsValue({ table: undefined })
            return this.getTableList(targetMap.sourceId, targetMap?.type?.schema)
        }
    }

    showCreateModal = () => {
        const { sourceMap, targetMap } = this.props
        const schema = this.props.form.getFieldValue('schema')
        this.setState({
            loading: true
        })
        const tableName = typeof sourceMap.type.table === 'string' ? sourceMap.type.table : sourceMap.type.table && sourceMap.type.table[0]
        const targetTableName = typeof targetMap.type.table === 'string' ? targetMap.type.table : targetMap.type.table && targetMap.type.table[0]
        ajax.getCreateTargetTable({
            originSourceId: sourceMap.sourceId,
            tableName,
            partition: sourceMap.type.partition,
            targetSourceId: targetMap.sourceId,
            originSchema: sourceMap?.type?.schema || null,
            targetSchema: schema || null
        }).then(
            (res: any) => {
                this.setState({
                    loading: false
                })
                if (res.code === 1) {
                    let textSql = res.data
                    if (targetTableName) {
                        const reg = /create\s+table\s+`(.*)`\s*\(/i
                        textSql = res.data.replace(reg, function (match: any, p1: any, offset: any, string: string) {
                            return match.replace(p1, targetTableName)
                        })
                    }
                    this.setState({
                        textSql: textSql,
                        sync: true,
                        visible: true
                    })
                }
            }
        )
    }

    ddlChange = (newVal: any) => {
        this.setState({
            textSql: newVal,
            sync: false
        })
    }

    versionDiff = (version1: any, version2: any) => {
        const v1 = version1.split('.')
        const v2 = version2.split('.')
        for (let i = 0; i < Math.max(v1.length, v2.length); i++) {
            const tempV1 = Number(v1[i] ?? 0)
            const tempV2 = Number(v2[i] ?? 0)
            if (tempV1 > tempV2) return true
            if (tempV1 < tempV2) return false
        }
        return true
    };

    checkData = (value: any) => {
        const { tableListSearch } = this.state
        const { setFieldsValue } = this.props.form

        if (!tableListSearch.includes(value)) {
            setFieldsValue({ table: undefined })
        }
    }

    renderTableList = (taskType: any) => {
        const { tableListSearch } = this.state
        return tableListSearch.map((table: any) => {
            return <Option
                key={`rdb-target-${table}`}
                value={table}>
                {table}
            </Option>
        })
    }

    render () {
        const { getFieldDecorator } = this.props.form
        const { tableListLoading } = this.state
        const {
            targetMap, dataSourceList, navtoStep
        } = this.props
        const getPopupContainer = this.props.getPopupContainer
        return <Spin spinning={tableListLoading}>
            <div className="g-step2">
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据同步目标"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{
                                required: true
                            }],
                            initialValue: isEmpty(targetMap) ? '' : `${targetMap.sourceId}`
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                onSelect={(value, options) => {
                                    this.setState({
                                        tableList: [],
                                        tableListSearch: []
                                    }, () => { this.changeSource(value, options) })
                                }}
                                optionFilterProp="name"
                            >
                                {dataSourceList.map((src: any) => {
                                    const title = `${src.dataName}（${(DATA_SOURCE_TEXT as any)[src.type]}）`

                                    return <Option
                                        key={src.id}
                                        {...{
                                            name: src.dataName,
                                            dataType: src.type
                                        }}
                                        value={`${src.id}`}>
                                        {title}
                                    </Option>
                                })}
                            </Select>
                        )}
                    </FormItem>
                    {this.renderDynamicForm()}
                    {!isEmpty(targetMap)
                        ? (
                            <FormItem
                                {...formItemLayout}
                                label="高级配置"
                            >
                                {getFieldDecorator('extralConfig', {
                                    rules: [{
                                        validator: formJsonValidator
                                    }],
                                    initialValue: get(targetMap, 'extralConfig', '')
                                })(
                                    <TextArea
                                        onChange={this.submitForm.bind(this)}
                                        placeholder={'以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize'}
                                        autosize={{ minRows: 2, maxRows: 6 }}
                                    />
                                )}
                                <HelpDoc doc={'dataSyncExtralConfigHelp'} />
                            </FormItem>
                        )
                        : null}
                </Form>
                {!this.props.readonly && <div className="steps-action">
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                    <Button
                        type="primary"
                        onClick={() => this.next(navtoStep)}
                        loading={this.state.loading}
                    >
                    下一步
                    </Button>
                </div>}
            </div>
        </Spin>
    }

    debounceTableSearch = debounce(this.changeTable, 600, { maxWait: 2000 })
    debounceTableNameSearch = debounce(this.getTableList, 500, { maxWait: 2000 });

    renderDynamicForm = () => {
        const { selectHack, loading, schemaList, kingbaseId, tableListSearch = ['111'], schemaId, tableList, fetching } = this.state

        const { targetMap, sourceMap, form } = this.props
        const { getFieldDecorator } = form
        const sourceType = sourceMap.type && sourceMap.type.type
        const targetType = targetMap?.type?.type
        // 是否拥有分区
        let formItem: any
        const havePartition = targetMap.type && (!!targetMap.type.partition || targetMap.type.havePartition)
        const getPopupContainer = this.props.getPopupContainer
        const showCreateTable = (
            targetType === DATA_SOURCE.HIVE_2 ||
            targetType === DATA_SOURCE.HIVE_3 ||
            targetType === DATA_SOURCE.HIVE_SERVER ||
            targetType === DATA_SOURCE.HIVE_1 ||
            targetType === DATA_SOURCE.POSTGRESQL ||
            targetType === DATA_SOURCE.MYSQL
        )
        const showCreateTableSource = (
            sourceType === DATA_SOURCE.MYSQL ||
            sourceType === DATA_SOURCE.ORACLE ||
            sourceType === DATA_SOURCE.SQLSERVER ||
            sourceType === DATA_SOURCE.POSTGRESQL ||
            sourceType === DATA_SOURCE.HIVE_2 ||
            sourceType === DATA_SOURCE.HIVE_3 ||
            sourceType === DATA_SOURCE.HIVE_SERVER ||
            sourceType === DATA_SOURCE.HIVE_1
        )

        const oneKeyCreateTable = showCreateTable && showCreateTableSource && (
            loading
                ? <Icon type="loading" />
                : <a
                    style={{ top: '0px', right: '-103px' }}
                    onClick={this.showCreateModal.bind(this)}
                    className="help-doc" >一键生成目标表</a>
        )

        if (isEmpty(targetMap)) return null
        switch (targetMap.type.type) {
            case DATA_SOURCE.MYSQL: {
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                optionFilterProp="value"
                                filterOption={false}
                                onSearch={(str: any) => {
                                    this.debounceTableNameSearch(targetMap.sourceId, null, str)
                                }}
                                onSelect={this.debounceTableSearch.bind(this, null)}
                                notFoundContent={fetching ? <Spin size="small" /> : null}
                            >
                                {/* {tableListSearch.map((table: any) => {
                                    return <Option
                                        key={`rdb-target-${table}`}
                                        value={table}>
                                        {table}
                                    </Option>
                                })} */}
                                <Option
                                    key={'rdb-1'}
                                    value={1}
                                >
                                    {1}
                                </Option>
                            </Select>
                        )}
                        { oneKeyCreateTable }
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入前准备语句"
                        key="preSql"
                    >
                        {getFieldDecorator('preSql', {
                            rules: [],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.preSql
                        })(
                            <Input.TextArea
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入导入数据前执行的SQL脚本"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入后准备语句"
                        key="postSql"
                    >
                        {getFieldDecorator('postSql', {
                            rules: [],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.postSql
                        })(
                            <Input.TextArea
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入导入数据后执行的SQL脚本"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label={'主键冲突'}
                        key="writeMode-mysql"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@mysql', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'insert'
                        })(
                            <Select onChange={this.submitForm.bind(this)}>
                                <Option key="writeModeInsert" value="insert">insert into（当主键/约束冲突，报脏数据）</Option>
                                <Option key="writeModeReplace" value="replace">replace into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）</Option>
                                <Option key="writeModeUpdate" value="update">on duplicate key update（当主键/约束冲突，update数据，未映射的字段值不变）</Option>
                            </Select>
                        )}
                    </FormItem>
                ]
                break
            }
            case DATA_SOURCE.ORACLE: {
                formItem = [
                    <FormItem {...formItemLayout} label="schema" key="schema">
                        {getFieldDecorator('schema', {
                            rules: [],
                            initialValue: isEmpty(targetMap)
                                ? ''
                                : targetMap.type.schema
                        })(
                            <Select
                                showSearch
                                {...{ showArrow: true }}
                                allowClear={true}
                                onChange={(val: any) => {
                                    this.getTableList(kingbaseId, val)
                                    form.setFieldsValue({ table: '' })
                                }}
                            >
                                {schemaList.map((copateValue: any, index: any) => {
                                    return (
                                        <Option
                                            key={`copate-${index}`}
                                            value={copateValue}
                                        >
                                            {/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
                                            { copateValue === 'ROW_NUMBER()' ? 'ROW_NUMBER' : copateValue}
                                        </Option>
                                    )
                                })}
                            </Select>
                        )}
                    </FormItem>,
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                optionFilterProp="value"
                                filterOption={false}
                                onSearch={(val: any) => this.debounceTableNameSearch(kingbaseId, schemaId, val)}
                                notFoundContent={fetching ? <Spin size="small" /> : null}
                                onSelect={this.debounceTableSearch.bind(this, null)}
                            >
                                {tableList.map((table: any) => {
                                    return <Option
                                        key={`rdb-target-${table}`}
                                        value={table}>
                                        {table}
                                    </Option>
                                })}
                            </Select>
                        )}
                        { oneKeyCreateTable }
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入前准备语句"
                        key="preSql"
                    >
                        {getFieldDecorator('preSql', {
                            rules: [],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.preSql
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入导入数据前执行的SQL脚本"
                                type="textarea"
                            ></Input>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入后准备语句"
                        key="postSql"
                    >
                        {getFieldDecorator('postSql', {
                            rules: [],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.postSql
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入导入数据后执行的SQL脚本"
                                type="textarea"
                            ></Input>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label={'主键冲突'}
                        key="writeMode-mysql"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@mysql', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'insert'
                        })(
                            <Select onChange={this.submitForm.bind(this)}>
                                <Option key="writeModeInsert" value="insert">insert into（当主键/约束冲突，报脏数据）</Option>
                                <Option key="writeModeReplace" value="replace">replace into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）</Option>
                                <Option key="writeModeUpdate" value="update">on duplicate key update（当主键/约束冲突，update数据，未映射的字段值不变）</Option>
                            </Select>
                        )}
                    </FormItem>
                ]
                break
            }
            case DATA_SOURCE.HIVE_1:
            case DATA_SOURCE.HIVE_2:
            case DATA_SOURCE.HIVE_3:
            case DATA_SOURCE.HIVE_SERVER: {
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                onBlur={this.checkData}
                                onSearch = {(str) => {
                                    this.onSearchTable(str)
                                }}
                                onSelect={this.debounceTableSearch.bind(this, null)}
                                notFoundContent={fetching ? <Spin size="small" /> : null}
                                optionFilterProp="value"
                            >
                                {tableListSearch.map((table: any) => {
                                    return <Option
                                        key={`rdb-target-${table}`}
                                        value={table}
                                    >
                                        {table}
                                    </Option>
                                })}
                            </Select>
                        )}
                        { oneKeyCreateTable }
                    </FormItem>,
                    havePartition
                        ? <FormItem
                            {...formItemLayout}
                            label="分区"
                            key="partition"
                        >
                            {getFieldDecorator('partition', {
                                rules: [{
                                    required: true,
                                    message: '目标分区为必填项！'
                                }],
                                initialValue: get(targetMap, 'type.partition', '')
                            })(
                                <AutoComplete
                                    showSearch
                                    {...{ showArrow: true }}
                                    placeholder="请填写分区信息"
                                    onChange={this.submitForm.bind(this)}
                                    filterOption={filterValueOption}
                                >
                                    {
                                        this.state.tablePartitionList.map((pt: any) => {
                                            return (
                                                <AutoComplete.Option
                                                    key={`rdb-${pt}`}
                                                    value={pt}
                                                >
                                                    {pt}
                                                </AutoComplete.Option>
                                            )
                                        })}
                                </AutoComplete>
                            )}
                            <HelpDoc doc="partitionDesc" />
                        </FormItem>
                        : '',
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        key="writeMode-hive"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@hive', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'replace'
                        })(
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value="replace" style={{ float: 'left' }}>
                                    覆盖（Insert Overwrite）
                                </Radio>
                                <Radio value="insert" style={{ float: 'left' }}>
                                    追加（Insert Into）
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ]
                break
            }
            case DATA_SOURCE.POSTGRESQL: {
                formItem = [
                    <FormItem {...formItemLayout} label="schema" key="schema">
                        {getFieldDecorator('schema', {
                            initialValue: isEmpty(targetMap)
                                ? ''
                                : targetMap?.schema ? targetMap?.schema : targetMap.type.schema
                        })(
                            <Select
                                showSearch
                                {...{ showArrow: true }}
                                allowClear={true}
                                onChange={(val: any) => {
                                    val && this.getTableList(kingbaseId, val)
                                    form.setFieldsValue({ table: '', splitPK: undefined })
                                    this.setState({
                                        tableListSearch: []
                                    })
                                }}
                            >
                                {schemaList.map((copateValue: any, index: any) => {
                                    return (
                                        <Option
                                            key={`copate-${index}`}
                                            value={copateValue}
                                        >
                                            {copateValue}
                                        </Option>
                                    )
                                })}
                            </Select>
                        )}
                    </FormItem>,
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                onSearch={(val: any) => this.debounceTableNameSearch(
                                    targetMap.sourceId, form.getFieldValue('schema'), val
                                )}
                                notFoundContent={fetching ? <Spin size="small" /> : null}
                                onSelect={this.debounceTableSearch.bind(this, null)}
                            >
                                {this.renderTableList(targetType)}
                            </Select>
                        )}
                        { oneKeyCreateTable }
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入前准备语句"
                        key="preSql"
                    >
                        {getFieldDecorator('preSql', {
                            rules: [],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.preSql
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入导入数据前执行的SQL脚本"
                                type="textarea"
                            ></Input>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入后准备语句"
                        key="postSql"
                    >
                        {getFieldDecorator('postSql', {
                            rules: [],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.postSql
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入导入数据后执行的SQL脚本"
                                type="textarea"
                            ></Input>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label={'主键冲突'}
                        key="writeMode-mysql"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@mysql', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'insert'
                        })(
                            <Select onChange={this.submitForm.bind(this)}>
                                <Option key="writeModeInsert" value="insert">insert into（当主键/约束冲突，报脏数据）</Option>,
                                <Option key="writeModeReplace" value="replace">replace into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）</Option>
                                <Option key="writeModeUpdate" value="update">on duplicate key update（当主键/约束冲突，update数据，未映射的字段值不变）</Option>
                            </Select>
                        )}
                    </FormItem>
                ]
                break
            }
            default: break
        }

        return formItem
    }
}

const TargetFormWrap = Form.create<any>()(TargetForm)

class Target extends React.Component<any, any> {
    render () {
        return <div>
            <TargetFormWrap {...this.props} />
        </div>
    }
}

const mapState = (state: any) => {
    const { workbench, dataSync } = state.dataSync
    const { isCurrentTabNew, currentTab } = workbench
    return {
        currentTab,
        isCurrentTabNew,
        project: state.project,
        projectTableTypes: state.tableTypes?.projectTableTypes || [],
        ...dataSync,
        dataSourceList: [{ dataDesc: '', createUserId: 5, gmtModified: 1598798357000, modifyUserId: 5, active: 0, dataName: 'test', dataJson: { jdbcUrl: 'jdbc:pivotal:greenplum://172.16.10.90:5432;DatabaseName=exampledb', username: 'gpadmin' }, gmtCreate: 1598798357000, type: 1, linkState: 0, modifyUser: { gmtModified: 1592466563000, phoneNumber: '17858263016', isDeleted: 0, id: 5, gmtCreate: 1592466563000, userName: 'admin@dtstack.com', dtuicUserId: 1, email: '123456.com@1.com', status: 0 }, isDefault: 1, tenantId: 3, id: 131, projectId: 95 }]
    }
}

const mapDispatch = (dispatch: any, ownProps: any) => {
    return {
        handleSourceChange (src: any) {
            dispatch({
                type: dataSyncAction.RESET_TARGET_MAP
            })
            dispatch({
                type: settingAction.INIT_CHANNEL_SETTING
            })
            dispatch({
                type: dataSyncAction.RESET_KEYMAP
            })
            dispatch({
                type: targetMapAction.DATA_SOURCE_TARGET_CHANGE,
                payload: src
            })
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            })
        },

        handleTargetMapChange (srcmap: any) {
            dispatch({
                type: targetMapAction.DATA_TARGETMAP_CHANGE,
                payload: srcmap
            })
        },
        updateTabAsUnSave () {
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            })
        },
        handleTableColumnChange: (colData: any) => {
            dispatch({
                type: dataSyncAction.RESET_KEYMAP
            })
            dispatch({
                type: targetMapAction.TARGET_TABLE_COLUMN_CHANGE,
                payload: colData
            })
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            })
        },
        changeNativeHive: (isNativeHive: any) => {
            dispatch({
                type: targetMapAction.CHANGE_NATIVE_HIVE,
                payload: isNativeHive
            })
        },
        updateTaskFields (params: any) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            })
        },
        getProjectTableTypes: (projectId: any) => {
            dispatch(getProjectTableTypes(projectId))
        },
        saveDataSyncToTab: (params: any) => {
            dispatch({
                type: workbenchAction.SAVE_DATASYNC_TO_TAB,
                payload: {
                    id: params.id,
                    data: params.data
                }
            })
        }
    }
}

export default connect(mapState, mapDispatch)(Target)
