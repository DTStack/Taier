import * as React from 'react';
import {
    Form, Select, Icon
} from 'antd';
import utils from 'dt-common/src/utils';
import {
    COMPONENT_TYPE_VALUE, COMPONEMT_CONFIG_KEYS, COMPONEMT_CONFIG_KEY_ENUM
} from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;
class DisplayResource extends React.Component<any, any> {
    renderDisplayResource = () => {
        const { components, getFieldDecorator, isView = false,
            fileChange, downloadFile, uploadLoading, componentConfig } = this.props;
        const componentTypeCode = components.componentTypeCode;
        // console.log(component.componentTypeCode)
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.SFTP:
                return (
                    <React.Fragment>
                        <FormItem
                            label="参数批量上传"
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.file`, null)(
                                <div>
                                    <label
                                        style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                                        className="ant-btn"
                                        htmlFor="mySftpFile"
                                    >
                                        <span>
                                            { uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
                                            上传文件
                                        </span>
                                    </label>
                                    <input
                                        name="file"
                                        type="file"
                                        id="mySftpFile"
                                        onChange={(e: any) => fileChange(e, components.componentTypeCode)}
                                        accept=".zip"
                                        style={{ display: 'none' }}
                                    />
                                    <span style={{ fontSize: 10, color: '#999' }}>仅支持json格式</span>
                                    {/* <div style={{ fontSize: 12, color: '#3F87FF' }}>{YarnFile.files && YarnFile.files[0] && YarnFile.files[0].name}</div> */}
                                </div>
                            )}
                        </FormItem>
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.YARN:
                const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
                // const file = config.fileName || '';
                const { fileName } = config;
                return (
                    <React.Fragment>
                        <FormItem
                            label="集群版本"
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.YARN}.hadoopVersion`, {
                                initialValue: components.hadoopVersion || 'hadoop2'
                            })(
                                <Select style={{ width: 172 }} disabled={isView}>
                                    <Option value='hadoop2' key='hadoop2'>hadoop2</Option>
                                    <Option value='hadoop3' key='hadoop3'>hadoop3</Option>
                                    <Option value='HW' key='HW'>HW</Option>
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            label="配置文件"
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.YARN}.file`, {
                                rules: [{
                                    required: true,
                                    message: '请上传配置文件'
                                }],
                                initialValue: components.uploadFileName || ''
                            })(
                                <div>
                                    <label
                                        style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                                        className="ant-btn"
                                        htmlFor="myYarnFile"
                                    >
                                        <span>
                                            { uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
                                            上传文件
                                        </span>
                                    </label>
                                    <input
                                        name="file"
                                        type="file"
                                        id="myYarnFile"
                                        onChange={(e: any) => fileChange(e, components.componentTypeCode)}
                                        accept=".zip"
                                        style={{ display: 'none' }}
                                    />
                                    <span style={{ fontSize: 10, color: '#999' }}>仅支持.zip格式，至少包括yarn-site.xml</span>
                                    {/* components.uploadFileName ||  */}
                                    { fileName && <div className="c-displayResource__downloadFile" onClick={() => downloadFile(components, 1)} style={{ fontSize: 12, color: '#3F87FF' }}>
                                        <span>
                                            <Icon type="paper-clip" style={{ marginRight: 2, color: '#666666FF' }} />
                                            {utils.textOverflowExchange(fileName, 10)}
                                            {/* {components.uploadFileName} */}
                                        </span>
                                        <Icon type="download" style={{ color: '#666666FF' }} />
                                    </div>}
                                </div>
                            )}
                        </FormItem>
                        <FormItem
                            label="Hadoop Kerberos认证文件"
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.YARN}.kerberosFileName`, null)(
                                <div>
                                    <label
                                        style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                                        className="ant-btn"
                                        htmlFor="myYarnKerberosFile"
                                    >
                                        <span>
                                            {/* { uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> } */}
                                            <Icon type="upload" style={{ marginRight: 8 }} />
                                            上传文件
                                        </span>
                                    </label>
                                    <input
                                        name="file"
                                        type="file"
                                        id="myYarnKerberosFile"
                                        // onChange={this.fileChange.bind(this)}
                                        accept=".zip"
                                        style={{ display: 'none' }}
                                    />
                                    <span style={{ fontSize: 10, color: '#999' }}>仅支持.zip格式</span>
                                    {/* <div style={{ fontSize: 12, color: '#3F87FF' }}>{YarnFile.files && YarnFile.files[0] && YarnFile.files[0].name}</div> */}
                                </div>
                            )}
                        </FormItem>
                    </React.Fragment>
                )
            default:
                return '';
        }
    }
    render () {
        return (
            <div className="c-displayResource__container">
                {this.renderDisplayResource()}
            </div>
        )
    }
}

export default DisplayResource;
