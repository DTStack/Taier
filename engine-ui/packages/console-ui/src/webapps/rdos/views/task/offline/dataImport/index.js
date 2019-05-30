import React, { Component } from 'react'
import { isEmpty } from 'lodash'
import { connect } from 'react-redux';
import {
    Modal, Button, message
} from 'antd';

import DataSource from './source'
import DataTarget from './target'
import API from '../../../../api/dataManage'

import { getUploadStatus } from '../../../../store/modules/uploader'

const defaultState = {
    file: '',
    startLine: 1,
    asTitle: true,
    visible: false,
    sourceFile: '',
    data: [],
    splitSymbol: ',',
    charset: 'UTF-8',
    step: 'source',
    engineType: '', // 引擎类型
    tableData: {}, // 表数据
    targetTable: '', // 目标表
    tableList: [], // table列表
    partitions: [], // 分区
    hasPartition: false,
    originPartitions: [],
    columnMap: [], // 映射
    matchType: 1, // 匹配方法
    sqlText: '', // SQL text
    sync: true, // editor sync sign
    queryTable: '', // 查询表
    overwriteFlag: 0, // 导入模式
    originLineCount: 0, // 原数据总条数
    targetExchangeWarning: false// target界面是否提示未选择源字段
}
@connect()
class ImportLocalData extends Component {
    state = Object.assign({}, defaultState)

    importData = () => {
        const { dispatch } = this.props;
        const { file } = this.state;
        const params = this.getParams()
        if (this.checkParams(params)) {
            params.partitions = JSON.stringify(params.partitions)
            params.keyRef = JSON.stringify(params.keyRef)
            this.setState({
                loading: true
            })
            API.importLocalData(params).then((res) => {
                this.setState({
                    loading: false
                })
                if (res.code === 1) {
                    getUploadStatus({
                        queryParams: { queryKey: res.data },
                        fileName: file.name
                    }, dispatch)
                    this.setState({
                        ...defaultState
                    })
                }
            })
        }
    }

    checkParams = (params) => {
        let flag = true
        const { originPartitions, columnMap, matchType } = this.state
        const partitions = params.partitions

        if (!params.engineType) {
            message.error('请选择目标引擎！')
            return false;
        }

        if (!params.tableId) {
            message.error('请选择要导入的目标表！')
            return false;
        }

        if (matchType == 1) {
            let isValueNull = true;

            for (let i = 0; i < columnMap.length; i++) {
                let item = columnMap[i];

                if (!isEmpty(item)) {
                    isValueNull = false;
                    break;
                }
            }

            if (isValueNull) {
                this.setState({
                    targetExchangeWarning: true
                })
                return false;
            }
        }

        if (originPartitions && originPartitions.length > 0) {
            let filledParitions = true;
            for (let i = 0; i < partitions.length; i++) {
                const item = partitions[i]
                const key = originPartitions[i].columnName
                if (item[key] === '') {
                    flag = false
                    filledParitions = false;
                }
            }
            if (!filledParitions) message.error('分区每项值必填！')
        }
        return flag
    }

    getParams = () => {
        const {
            file, targetTable, splitSymbol,
            charset, startLine, asTitle,
            matchType, columnMap, partitions,
            overwriteFlag, engineType
        } = this.state
        return {
            tableId: targetTable.id,
            separator: splitSymbol,
            oriCharset: charset,
            topLineIsTitle: asTitle,
            keyRef: columnMap,
            file,
            matchType,
            startLine,
            partitions,
            engineType,
            overwriteFlag
        }
    }

    fileChange = (e) => {
        const file = e.target.files[0];
        const sizeLimit = 50 * 1024 * 1024 // 50MB
        if (file.size > sizeLimit) {
            message.error('本地上传文件不可超过50MB!')
        } else {
            this.setState({ file: file }, () => {
                this.readFile(file)
            })
        }
    }

    fileClick = (e) => {
        e.target.value = null;
        this.setState(Object.assign({}, defaultState));
    }

    readFile = (file) => {
        const { charset } = this.state
        if (file) {
            const reader = new FileReader();
            reader.onload = ((data) => {
                return (e) => {
                    this.setState({
                        sourceFile: e.target.result
                    })
                    this.parseFile(e.target.result)
                }
            })(file)
            reader.readAsText(file, charset)
        }
    }

    parseFile (data) {
        const { splitSymbol, startLine } = this.state
        const arr = []
        const splitVal = this.parseSplitSymbol(splitSymbol)

        data = data.replace(/\r\n/g, '\n').replace(/\r/g, '\n').split('\n');

        // 防卡死
        if (data && data[0].length > 5000) {
            message.error('文件内容不正确！');
            return;
        }

        for (let i = 0; i < data.length; i++) {
            const str = data[i].replace(/\r/, '') // 清除无用\r字符
            if (str) {
                arr.push(str.split(splitVal))
            }
        }

        const subArr = arr.slice(startLine - 1)

        this.setState({
            data: subArr || [],
            step: 'source',
            visible: true,
            originLineCount: data.length
        })
    }

    parseSplitSymbol (value) {
        switch (value) {
            case 'blank':
                value = ' '
                break;
            case 'tab':
                value = '\t'
        }
        return value
    }

    changeStatus = (items) => {
        const { file } = this.state
        console.log('changeStatus', items)
        this.setState(items, () => {
            this.readFile(file)
        })
    }

    next = () => {
        this.setState({ step: 'target' })
    }

    prev = () => {
        this.setState({ step: 'source' })
    }

    onCancel = () => {
        this.setState({ ...defaultState });
    }

    footer () {
        const { step, loading } = this.state
        return (
            <div>
                <Button onClick={() => {
                    this.setState({ ...defaultState })
                }}>取消</Button>
                <Button
                    style={{
                        display: step === 'source'
                            ? 'inline-block' : 'none'
                    }}
                    type="primary"
                    onClick={this.next}>
                    下一步
                </Button>
                <Button
                    style={{
                        display: step === 'target'
                            ? 'inline-block' : 'none'
                    }}
                    type="primary"
                    onClick={this.prev}>
                    上一步
                </Button>
                <Button
                    style={{
                        display: step === 'target'
                            ? 'inline-block' : 'none'
                    }}
                    onClick={this.importData}
                    loading={loading}
                    type="primary">
                    导入
                </Button>
            </div>
        )
    }

    render () {
        const { data, file, visible, step, targetExchangeWarning } = this.state
        return (
            <div id="JS_import_modal">
                <input
                    name="file"
                    type="file"
                    accept=".txt, .log, .csv"
                    id="JS_importFile"
                    onClick={this.fileClick}
                    onChange={this.fileChange}
                    style={{ display: 'none' }}
                />
                <Modal
                    maskClosable={false}
                    title="本地数据导入"
                    visible={visible}
                    onCancel={this.onCancel}
                    footer={this.footer()}
                >
                    <DataSource
                        data={data}
                        file={file}
                        display={step}
                        formState={this.state}
                        changeStatus={this.changeStatus}
                    />
                    <DataTarget
                        visible={visible}
                        warning={targetExchangeWarning}
                        data={data}
                        file={file}
                        formState={this.state}
                        changeStatus={(target) => {
                            this.setState(target)
                        }}
                        display={step}
                    />
                </Modal>
            </div>
        )
    }
}
export default ImportLocalData;
