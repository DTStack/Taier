import * as React from 'react'
import { Modal, Button, Collapse, Input, Icon, Spin } from 'antd';
import { assign } from 'lodash';

import utils from 'utils';

import Api from '../../../api';

const Panel = Collapse.Panel;
const TextArea = Input.TextArea;

class DataPreviewModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props)
        this.state = {
            loading: false,
            previewData: [] // panel数据，若props传入dataSource，数据则为dataSource
        };
        this.retryCount = 0;
    }
    retryCount: number;
    MAX_RETRY_COUNT: number = 3;
    _clock: any;
    componentDidUpdate (prevProps: any) {
        const params = this.props.params
        if (prevProps.visible != this.props.visible && this.props.visible && params) {
            this.setState({
                previewData: []
            })
            clearTimeout(this._clock);
            this.retryCount = 0;
            this.getDataPreviewList(params);
        }
    }
    componentWillUnmount () {
        clearTimeout(this._clock);
    }
    getDataPreviewList = (params: any) => {
        this.setState({
            loading: true
        })
        Api.getDataPreview(params).then((res: any) => {
            if (params != this.props.params) {
                return;
            }
            if (res.code === 1) {
                if (res.data) {
                    this.setState({
                        previewData: res.data,
                        loading: false
                    })
                } else if (this.retryCount < this.MAX_RETRY_COUNT) {
                    this._clock = setTimeout(() => {
                        this.getDataPreviewList(params)
                    }, 1000)
                    this.retryCount++;
                } else {
                    this.setState({
                        loading: false
                    })
                }
            } else {
                this.setState({
                    loading: false
                })
            }
        })
    }
    render () {
        const { visible, onCancel, className, style, dataSource } = this.props;
        const { previewData, loading } = this.state;
        let defaultStyle: any = {
            maxHeight: '300px',
            minHeight: '200px'
        }
        let defaultClass = 'ellipsis';
        if (style) defaultStyle = assign(defaultStyle, style)
        if (className) defaultClass = `${defaultClass} ${className}`
        return (
            <Modal
                visible={visible}
                title='数据预览'
                onCancel={onCancel}
                maskClosable={true}
                footer={[
                    <Button key='back' type="primary" onClick={onCancel}>关闭</Button>
                ]}
            >
                {
                    ((dataSource && dataSource.length > 0) || previewData.length > 0) ? (
                        <Collapse accordion>
                            {
                                (dataSource || previewData).map((item: any, index: any) => {
                                    const jsonStr = utils.isJSONStr(item) ? utils.jsonFormat(item, 4) : item;
                                    return (
                                        <Panel
                                            header={<div className={defaultClass}>{item || '无数据'}</div>}
                                            key={`preview-${index + 1}`}
                                        >
                                            <TextArea
                                                style={defaultStyle}
                                                value={jsonStr}
                                            />
                                        </Panel>
                                    )
                                })
                            }
                        </Collapse>
                    ) : (
                            <div style={{ textAlign: 'center' }}>{loading ? <React.Fragment>
                                <Spin size="small" tip="加载中..." />
                        </React.Fragment> : <React.Fragment>
                                <Icon type="frown-o" /> 暂无数据
                        </React.Fragment>} </div>
                        )
                }
            </Modal>
        )
    }
}

export default DataPreviewModal;
