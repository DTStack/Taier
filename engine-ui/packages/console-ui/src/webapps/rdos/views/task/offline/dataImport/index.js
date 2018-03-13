import React, { Component } from 'react'

import { 
    Modal, Button, Form,
    Icon, Input, Select,  
    message, Spin
} from 'antd';

import DataSource from './source'
import DataTarget from './target'
import API from '../../../../api'

export default class ImportLocalData extends Component {

    state = {
        file: '',
        startLine: 1,
        asTitle: true,
        visible: false,
        data: [],
        splitSymbol: ',',
        charset: 'UTF-8',
        step: 'source',
        tableData: {}, // 表数据
        targetTable: '', // 目标表
        tableList: [], // table列表
        partitions: [], // 分区
        hasPartition: false,
        originPartitions: [],
        columnMap: [], // 映射
        matchType: 1, // 匹配方法
        sqlText: '', // SQL text
        queryTable: '', // 查询表
        overwriteFlag: 0, // 导入模式
    }

    importData = () => {
        const params = this.getParams()
        if (this.checkParams(params)) {
            params.partitions = JSON.stringify(params.partitions)
            params.keyRef = JSON.stringify(params.keyRef)
            API.importLocalData(params).then((res) => {
                if (res.code === 1) {
                    const msg = `您已经成功导入${res.data}条数据！`
                    message.success(msg);
                    this.setState({
                        visible: false,
                    })
                }
            })
        }
    }

    checkParams = (params) => {
        let flag = true
        const { originPartitions } = this.state
        const partitions = params.partitions

        if (!params.tableId) {
            message.error('请选择要导入的目标表！')
            return false;
        }

        if (originPartitions && originPartitions.length > 0) {
            for (let i = 0; i < partitions.length; i++) {
                const item = partitions[i]
                const key = originPartitions[i].name
                if (item[key] === '') {
                    message.error('分区每项值必填！')
                    flag = false
                }
            }
        }
        return flag
    }

    getParams = () => {
        const { 
            file, targetTable, splitSymbol,
            charset, startLine, asTitle,
            matchType, columnMap, partitions,
            overwriteFlag,
        } = this.state
        return {
            tableId: targetTable.tableId,
            separator: splitSymbol,
            oriCharset: charset,
            topLineIsTitle: asTitle,
            keyRef: columnMap,
            file,
            matchType,
            startLine,
            partitions,
            overwriteFlag,
        }
    }

    fileChange = (e) => {
        const file = e.target.files[0];
        this.setState({ file: file }, () => {
            this.readFile(file)
        })
    }

    fileClick = (e) => {
        e.target.value = null;
        this.setState({
            file: '',
            startLine: 1,
            asTitle: true,
            visible: false,
            data: [],
            splitSymbol: ',',
            charset: 'UTF-8',
            step: 'source',
            targetTable: '', // 目标表
            partitions: [], // 分区
            hasPartition: false,
            originPartitions: [],
            columnMap: [], // 映射
            matchType: 1, // 匹配方法
            sqlText: '',
            queryTable: '',
            tableData: {},
            tableList: [],
        })
    }
    
    readFile = (file) => {
        const { charset } = this.state
        if (file) {
            const reader = new FileReader();
            const type = file.type.split('/')[1];
            reader.onload = ((data) => {
                return (e) => {
                    const result = this.parseFile(e.target.result, type) 
                    this.setState({ 
                        data: result, 
                        step: 'source',
                        visible: true, 
                    })
                }
            })(file)
            reader.readAsText(file, charset)
        }
    }

    parseFile(data, type) {
        const { splitSymbol, startLine } = this.state
        const arr = []
        const splitVal = this.parseSplitSymbol(splitSymbol)
        data = data.split('\n')
        for (let i = 0; i < data.length; i++) {
            const str = data[i].replace(/\r/, '') // 清除无用\r字符
            arr.push(str.split(splitVal))
        } 
        const subArr = arr.slice(startLine - 1)
        return subArr;
    }

    parseSplitSymbol(value) {
        switch(value) {
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

    footer () {
        const { step } = this.state
        return (
            <div>
                <Button 
                    style={{display: step === 'source' 
                    ? 'inline-block' : 'none'}}
                    type="primary" 
                    onClick={this.next}>
                    下一步
                </Button>
                <Button 
                    style={{display: step === 'target' 
                    ? 'inline-block' : 'none'}}
                    type="primary" 
                    onClick={this.prev}>
                    上一步
                </Button>
                <Button
                    style={{display: step === 'target' 
                    ? 'inline-block' : 'none'}}
                    onClick={this.importData}
                    type="primary">
                    导入
                </Button>
                <Button onClick={() => { 
                    this.setState({ visible: false })
                }}>取消</Button>
            </div>
        )
    }

    render() {
        const { data, file, visible, step } = this.state
        return (
            <div>
                <input
                    name="file"
                    type="file"
                    accept=".txt,.log,.svg"
                    id="importFile"
                    onClick={this.fileClick}
                    onChange={this.fileChange}
                    style={{ display: 'none' }}
                />
                <Modal
                    title="本地数据导入"
                    visible={visible}
                    onCancel={() => {
                        this.setState({ visible: false })
                    }}
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