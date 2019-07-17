import React from 'react';

import { Button, Checkbox, Spin } from 'antd';
import Editor from 'widgets/editor';
import API from '../../../../../api/apiManage';

class WebserviceView extends React.Component {
    state = {
        loading: false
    }
    getWsdlXml = () => {
        const { basicProperties, updateData } = this.props;
        const reqUrl = basicProperties.originalHost + basicProperties.originalPath;
        this.setState({
            loading: true
        })
        API.getWsdlXml({ reqUrl }).then((res) => {
            console.log(res);
            updateData({
                wsdlXml: res.data
            });
        }).finally(() => {
            this.setState({
                loading: false
            })
        })
    }
    render () {
        let { data = {}, basicProperties, updateData } = this.props;
        console.log(basicProperties, data)
        return (
            <React.Fragment>
                <div
                    style={{
                        width: '350px',
                        marginRight: '100px'
                    }}
                >
                    <div
                        style={{
                            fontSize: '18px'
                        }}
                    >
                        API信息：
                    </div>
                    <div
                        style={{
                            fontSize: '14px',
                            marginTop: '20px',
                            wordBreak: 'break-word'
                        }}
                    >
                        Webservice接口地址:
                        <br/>
                        {basicProperties.originalHost}{basicProperties.originalPath}
                    </div>
                    <div
                        style={{
                            display: 'flex',
                            flexDirection: 'row-reverse'
                        }}
                    >
                        <Button
                            style={{
                                marginTop: '40px',
                                fontSize: '14px',
                                height: '30px'
                            }}
                            onClick={this.getWsdlXml}
                        >
                            查看接口规范
                        </Button>
                    </div>
                </div>
                <div
                    style={{
                        flex: 1
                    }}
                >
                    <div
                        style={{
                            fontSize: '18px'
                        }}
                    >
                        接口调用规范:
                    </div>
                    <div
                        style={{
                            position: 'relative',
                            border: '1px solid #DDDDDD',
                            margin: '10px 0'
                        }}
                    >
                        <div
                            style={{
                                fontSize: '14px',
                                background: '#f0f0f0',
                                height: '35px',
                                lineHeight: '35px',
                                paddingLeft: '10px'
                            }}
                        >
                            接口详情
                        </div>
                        <Spin
                            spinning={this.state.loading}
                        >
                            {
                                data.wsdlXml
                                    ? (
                                        <Editor
                                            sync={true}
                                            // onChange={this.editorChange.bind(this)}
                                            // key={data.wsdlXml}
                                            options={{ readOnly: true }}
                                            language='xml'
                                            style={{
                                                height: '420px'
                                            }}
                                            disabled={true}
                                            value={data.wsdlXml || '暂无内容'}
                                        />
                                    )
                                    : (
                                        <div
                                            style={{
                                                color: '#ccc',
                                                fontSize: '14px',
                                                height: '420px',
                                                textAlign: 'center',
                                                lineHeight: '420px'
                                            }}
                                        >
                                            暂无内容
                                        </div>
                                    )
                            }
                        </Spin>
                    </div>
                    {
                        data.wsdlXml && data.wsdlXml !== ''
                            ? (
                                <Checkbox
                                    style={{
                                        marginTop: '10px',
                                        background: 'none'
                                    }}
                                    checked={data.saveWsdlXml === 1}
                                    onChange={(e) => {
                                        console.log(e.target.checked);
                                        updateData({
                                            saveWsdlXml: e.target.checked ? 1 : 0
                                        });
                                    }}
                                >
                                    保存接口规范至API详情中，方便API申请者调用
                                </Checkbox>
                            )
                            : null
                    }
                </div>
            </React.Fragment>
        )
    }
}
export default WebserviceView;
