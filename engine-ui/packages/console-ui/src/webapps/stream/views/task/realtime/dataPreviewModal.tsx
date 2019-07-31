import * as React from 'react'
import { Modal, Button, Collapse, Input, Icon } from 'antd';
import { assign } from 'lodash';

import utils from 'utils';

import Api from '../../../api';

const Panel = Collapse.Panel;
const TextArea = Input.TextArea;

class DataPreviewModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            previewData: [] // panel数据，若props传入dataSource，数据则为dataSource
        };
    }
    /* eslint-disable-next-line */
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const params = nextProps.params
        if (this.props.visible != nextProps.visible && nextProps.visible && params) {
            this.setState({
                previewData: []
            })
            this.getDataPreviewList(params);
        }
    }
    getDataPreviewList = (params: any) => {
        Api.getDataPreview(params).then((res: any) => {
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
                        <div style={{ textAlign: 'center' }}><Icon type="frown-o" />  暂无数据</div>
                    )
                }
            </Modal>
        )
    }
}

export default DataPreviewModal;
