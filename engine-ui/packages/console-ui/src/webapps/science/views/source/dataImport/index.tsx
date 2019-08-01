import * as React from 'react'
import { isEmpty } from 'lodash'
import { connect } from 'react-redux';
import {
    Modal, Button, message, Alert
} from 'antd';

import DataSource from './source'
import DataTarget from './target'
import API from '../../../api/table';
import { toRdosGateway } from 'funcs';
import { appUriDict } from 'main/consts';

// import { getUploadStatus } from '../../../actions/sourceActions'

const defaultState: any = {
    file: '',
    startLine: 1,
    asTitle: true,
    visible: false,
    sourceFile: '',
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
    sync: true, // editor sync sign
    queryTable: '', // 查询表
    overwriteFlag: 0, // 导入模式
    originLineCount: 0, // 原数据总条数
    targetExchangeWarning: false// target界面是否提示未选择源字段
}
@(connect((state: any) => {
    return {
        currentProject: state.project.currentProject
    }
}) as any)
class ImportLocalData extends React.Component<any, any> {
    state = Object.assign({ key: Math.random() }, defaultState)

    importData = () => {
        // const { dispatch } = this.props;
        // const { file } = this.state;
        const params = this.getParams()
        if (this.checkParams(params)) {
            params.partitions = JSON.stringify(params.partitions)
            params.keyRef = JSON.stringify(params.keyRef)
            this.setState({
                loading: true
            })
            API.importLocalData(params).then((res: any) => {
                this.setState({
                    loading: false
                })
                if (res.code === 1) {
                    // getUploadStatus({
                    //     queryParams: { queryKey: res.data },
                    //     fileName: file.name
                    // }, dispatch)
                    this.setState({
                        ...defaultState,
                        key: Math.random()
                    });
                    this.props.onOk();
                }
            })
        }
    }

    checkParams = (params: any) => {
        let flag = true
        const { originPartitions, columnMap, matchType } = this.state
        const partitions = params.partitions

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
            overwriteFlag
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
            overwriteFlag
        }
    }

    fileChange = (e: any) => {
        const file = e.target.files[0];
        const sizeLimit = 20 * 1024 * 1024 // 20MB
        const fileName = file.name;
        const suffix = fileName.split('.').pop();
        const whiteList: any = ['txt', 'csv'];
        if (whiteList.indexOf(suffix) == -1) {
            message.error(`不支持 ${suffix} 后缀的文件`);
            return;
        }
        if (file.size > sizeLimit) {
            message.error('本地上传文件不可超过20MB!')
        } else {
            this.setState({ file: file }, () => {
                this.readFile(file)
            })
        }
    }

    fileClick = (e: any) => {
        e.target.value = null;
        this.setState(Object.assign({}, defaultState));
    }

    readFile = (file: any) => {
        const { charset } = this.state
        if (file) {
            const reader = new FileReader();
            reader.onload = ((data: any) => {
                return (e: any) => {
                    this.setState({
                        sourceFile: e.target.result
                    })
                    this.parseFile(e.target.result)
                }
            })(file)
            reader.readAsText(file, charset)
        }
    }

    parseFile(data: any) {
        const { splitSymbol, startLine } = this.state
        const arr: any = []
        const splitVal = this.parseSplitSymbol(splitSymbol)
        data = data.replace(/\r\n/g, '\n').replace(/\r/g, '\n').split('\n');

        // 防卡死
        if (data && data[0].length > 5000) {
            message.error('文件字段超出限制！');
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

    parseSplitSymbol(value: any) {
        switch (value) {
            case 'blank':
                value = ' '
                break;
            case 'tab':
                value = '\t'
        }
        return value
    }

    changeStatus = (items: any) => {
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
        this.setState({ ...defaultState, key: Math.random() });
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
    renderTips = () => {
        const { currentProject } = this.props;
        const { step } = this.state;
        const message = (
            <div style={{ color: '#666666' }}>
                {
                    step === 'source'
                        ? (
                            <>
                                此模块支持本地小批量数据上传，若需要同步数据库数据或大批量数据，请前往
                                <a onClick={toRdosGateway.bind(null, appUriDict.RDOS.DEVELOP, { projectId: currentProject.refProjectId })}>离线计算-数据同步</a>
                                模块完成
                            </>
                        )
                        : <>本地文件可导入至HDFS的数据表，也可不导入数据表，直接将文件上传至HDFS。</>
                }
            </div>
        )
        let key = 'alert';
        if (step === 'target' && document.querySelectorAll('.ant-alert').length === 0) {
            key = 'reAlert';
        }
        return (
            <Alert
                key={key}
                style={{ marginBottom: 16 }}
                message={message}
                type="info"
                showIcon
                closable />
        )
    }
    render () {
        const { data, file, visible, step, targetExchangeWarning, key } = this.state
        return (
            <div id="JS_import_modal">
                <input
                    name="file"
                    type="file"
                    accept=".txt, .csv"
                    id="JS_importFile"
                    onClick={this.fileClick}
                    onChange={this.fileChange}
                    style={{ display: 'none' }}
                />
                <Modal
                    maskClosable={false}
                    title="本地数据导入"
                    visible={visible}
                    key={key}
                    onCancel={this.onCancel}
                    footer={this.footer()}
                >
                    {this.renderTips()}
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
                        changeStatus={(target: any) => {
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
