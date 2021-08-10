import React, { useState } from 'react'
import { Modal, Row, Col, Input, Button, Spin, Form } from 'antd'
import HelpDoc from '../../components/helpDoc'
import ajax from '../../api'

const formItemLayout: any = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 4 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
    }
}
function ShowPreviewPath (props: any) {
    const { visible, handleCancel, previewPath, sourceId, taskVariables, form } = props
    const [list, setList] = useState([])
    const [loading, setLoading] = useState(false)
    const { getFieldDecorator, validateFields } = form
    const [num, setNum] = useState(0)
    const searchPath = async () => {
        validateFields(async (err: any, values: any) => {
            if (!err) {
                setList([])
                setLoading(true)
                const res = await ajax.ftpRegexPre({
                    sourceId,
                    regexStr: form?.getFieldValue('regexStr'),
                    taskParamList: taskVariables
                })
                const { data } = res
                if (res?.code === 1) {
                    const { fileNameList = [], number } = data
                    if (Array.isArray(fileNameList) && fileNameList.length > 0) {
                        setList(fileNameList as any)
                        setNum(number || 0)
                    }
                }
                setLoading(false)
            }
        })
    }
    const textStyle : any = {
        'line-height': 32
    }
    return (
        <Modal
            title="匹配预览"
            visible={ visible }
            footer={false}
            onCancel={handleCancel}
        >
            <Form
                style={{ position: 'relative' }}
            >
                <Form.Item
                    {...formItemLayout}
                    label={
                        <span>
                            <span style={{ marginRight: 18 }}>路径</span>
                            <HelpDoc doc="ftpRegexPre" style={{ top: 2, right: 14 }} />
                        </span>
                    }
                >
                    {getFieldDecorator('regexStr', {
                        rules: [{ required: true, message: '请输入路径进行匹配' }],
                        initialValue: previewPath || ''
                    })(
                        <Input style={{ width: 272 }} />
                    )}
                    <Button style={{ position: 'absolute', top: 0, right: -127 }} ghost onClick={searchPath} >执行匹配</Button>
                </Form.Item>
            </Form>
            <Row>
                <Col span={4} style={{ textAlign: 'right' }} >
                    <p style={{ lineHeight: '32px', marginRight: 8 }}>匹配结果: </p>
                </Col>
                <Col span={20}>
                    {
                        loading
                            ? <Spin />
                            : <p style={textStyle}>
                                {
                                    num > 0
                                        ? <>
                                            <span style={{ color: '#3F87FF' }}>{ num > 100 ? '100+' : num} </span>Object
                                            {num > 20 && `（${num > 100 ? '已停止匹配，' : ''}以下随机列出 20 个匹配结果)`}
                                        </>
                                        : <span style={{ paddingLeft: 12 }}> 无 </span>
                                }
                            </p>
                    }
                    {
                        num > 0 && (
                            <div className="regExpModal_info__normal">
                                {
                                    list.map((item, index) => index < 20 ? <p key={item}>{item}</p> : null)
                                }
                            </div>)
                    }
                </Col>
            </Row>
        </Modal>
    )
}

export default Form.create({
})(ShowPreviewPath)
