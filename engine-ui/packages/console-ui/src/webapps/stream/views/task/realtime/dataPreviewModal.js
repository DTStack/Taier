import React, { Component } from 'react'
import { Modal, Button, Collapse, Input } from 'antd';
/* eslint-disable */
const Panel = Collapse.Panel;
const TextArea = Input.TextArea;
class DataPreviewModal extends Component {
    constructor (props) {
        super(props)
        this.state = {
            // mock
            previewData: [{
                "Config": {
                    "ha_rm_id1": {
                        "Default": "rm1",
                        "Desc": "internal",
                        "Type": "internal",
                        "Value": "rm1"
                    },
                    "ha_rm_id2": {
                        "Default": "rm2",
                        "Desc": "internal",
                        "Type": "internal",
                        "Value": "rm2"
                    },
                    "jobhistory_ip": {
                        "Default": {
                            "Host": ["172-16-10-107"],
                            "IP": ["172.16.10.107"],
                            "NodeId": 1,
                            "SingleIndex": 0
                        },
                        "Desc": "internal",
                        "Type": "internal",
                        "Value": {
                            "Host": ["172-16-10-107"],
                            "IP": ["172.16.10.107"],
                            "NodeId": 1,
                            "SingleIndex": 0
                        }
                    },
                    "resourcemanager_ip": {
                        "Default": {
                            "Host": ["172-16-10-107", "172-16-10-16"],
                            "IP": ["172.16.10.107", "172.16.10.16"],
                            "NodeId": 2,
                            "SingleIndex": 1
                        },
                        "Desc": "internal",
                        "Type": "internal",
                        "Value": {
                            "Host": ["172-16-10-107", "172-16-10-16"],
                            "IP": ["172.16.10.107", "172.16.10.16"],
                            "NodeId": 2,
                            "SingleIndex": 1
                        }
                    },
                    "zk_ip": {
                        "Default": {
                            "Host": ["172-16-10-107", "172-16-10-108", "172-16-10-16"],
                            "IP": ["172.16.10.107", "172.16.10.108", "172.16.10.16"],
                            "NodeId": 1,
                            "SingleIndex": 0
                        },
                        "Desc": "internal",
                        "Type": "internal",
                        "Value": {
                            "Host": ["172-16-10-107", "172-16-10-108", "172-16-10-16"],
                            "IP": ["172.16.10.107", "172.16.10.108", "172.16.10.16"],
                            "NodeId": 1,
                            "SingleIndex": 0
                        }
                    }
                }
            }, {
                "resourcemanager_ip": {
                    "Default": {
                        "Host": ["172-16-10-107", "172-16-10-16"],
                        "IP": ["172.16.10.107", "172.16.10.16"],
                        "NodeId": 2,
                        "SingleIndex": 1
                    },
                    "Desc": "internal",
                    "Type": "internal",
                    "Value": {
                        "Host": ["172-16-10-107", "172-16-10-16"],
                        "IP": ["172.16.10.107", "172.16.10.16"],
                        "NodeId": 2,
                        "SingleIndex": 1
                    }
                }
            }]
        };
    }
    render () {
        const { visible, onCancel } = this.props;
        const { previewData } = this.state;
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
                <Collapse accordion>
                    {
                        previewData.map((item, index) => {
                            console.log(item)
                            return (
                                <Panel
                                    header={<div style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', width: '450px' }}>{JSON.stringify(item)}</div>}
                                    key={index + 1 + ''}
                                >
                                    <TextArea
                                        style={{maxHeight: '300px', minHeight: '200px' }}
                                        value={JSON.stringify(item, null, 4)}
                                    />
                                </Panel>
                            )
                        })
                    }
                </Collapse>
            </Modal>
        )
    }
}

export default DataPreviewModal;
