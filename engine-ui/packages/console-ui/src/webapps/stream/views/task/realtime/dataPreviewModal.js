import React, { Component } from 'react'
import { Modal, Button, Collapse, Input, Icon } from 'antd';
import { assign } from 'lodash'
import Api from '../../../api';
const Panel = Collapse.Panel;
const TextArea = Input.TextArea;
class DataPreviewModal extends Component {
    constructor (props) {
        super(props)
        this.state = {
            previewData: [] // panel数据，若props传入dataSource，数据则为dataSource
        };
    }
    /* eslint-disable-next-line */
    UNSAFE_componentWillReceiveProps(nextProps) {
        const params = nextProps.params
        if (this.props.visible != nextProps.visible && nextProps.visible && params) {
            this.setState({
                previewData: []
            })
            this.getDataPreviewList(params);
        }
    }
    getDataPreviewList = (params) => {
        Api.getDataPreview(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    previewData: res.data || []
                })
            }
        })
    }
    render () {
        const { visible, onCancel, className, style, dataSource } = this.props;
        const { previewData } = this.state;
        let defaultStyle = {
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
                maskClosable={false}
                footer={[
                    <Button key='back' type="primary" onClick={onCancel}>关闭</Button>
                ]}
            >
                {
                    ((dataSource && dataSource.length > 0) || previewData.length > 0) ? (
                        <Collapse accordion>
                            {
                                (dataSource || previewData).map((item, index) => {
                                    return (
                                        <Panel
                                            header={<div className={defaultClass}>{JSON.stringify(item)}</div>}
                                            key={index + 1 + ''}
                                        >
                                            <TextArea
                                                style={defaultStyle}
                                                value={JSON.stringify(item, null, 4)}
                                            />
                                        </Panel>
                                    )
                                })
                            }
                        </Collapse>
                    ) : (
                        <div style={{ textAlign: 'center' }}><Icon type="frown-o" />  暂无数据</div>
                    )
                }
            </Modal>
        )
    }
}

export default DataPreviewModal;
