import * as React from 'react';
import Editor from 'widgets/editor';
import { generateUrlQuery, generateHeader, generateTokenHeader } from './helper';

class Webservice extends React.Component<any, any> {
    render () {
        const { isManage, getValue, registerInfo = {} } = this.props;
        let inputParam = isManage ? registerInfo.inputParam : getValue('reqParam');
        let inputColumn = inputParam || [];
        inputColumn = inputColumn.filter((column: any) => {
            return !column.constant
        })
        console.log(this.props)
        return (
            <div>
                 <div style={{ overflow: 'hidden' }}>
                    <section className='c-content-register__section c_left__section'>
                        <h1 className="title-border-l-blue">请求示例</h1>
                        <p className='c_title_method'>方式一：AK/SK签名加密方式</p>
                        <div style={{ marginTop: '12px' }} className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Request URL</div>
                            <div className='c-content-register__section__card__content'>
                                {generateUrlQuery(inputColumn)}
                            </div>
                        </div>
                        <div className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Headers</div>
                            <div className='c-content-register__section__card__content'>
                                {generateHeader(inputColumn)}
                            </div>
                        </div>
                        <div className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Body</div>
                            <div className='c-content-register__section__card__content'>
                                请在接口规范中查看Body内容
                            </div>
                        </div>
                    </section>
                    <section className='c-content-register__section c_left__section' style={{ margin: '53px 0 0 20px' }}>
                        <p className='c_title_method'>方式二：TOKEN加密方式</p>
                        <div style={{ marginTop: '12px' }} className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Request URL</div>
                            <div className='c-content-register__section__card__content'>
                                {generateUrlQuery(inputColumn)}
                            </div>
                        </div>
                        <div className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Headers</div>
                            <div className='c-content-register__section__card__content'>
                                {generateTokenHeader(inputColumn)}
                            </div>
                        </div>
                        <div className='c-content-register__section__card'>
                            <div className='c-content-register__section__card__title c__section__card__title--bold'>Body</div>
                            <div className='c-content-register__section__card__content'>
                                请在接口规范中查看Body内容
                            </div>
                        </div>
                    </section>
                </div>
                <section
                    style={{
                        marginTop: '40px'
                    }}
                >
                    <h1 className="title-border-l-blue">接口规范</h1>
                    <div
                        style={{
                            position: 'relative',
                            border: '1px solid #DDDDDD',
                            margin: '10px 0',
                            marginRight: '20px'
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
                        {
                            getValue('saveWsdlXml') === 1 && getValue('wsdlXml')
                                ? (
                                    <Editor
                                        sync={true}
                                        // onChange={this.editorChange.bind(this)}
                                        // key={data.wsdlXml}
                                        language='xml'
                                        style={{
                                            height: '420px'
                                        }}
                                        options={{ readOnly: true }}
                                        disabled={true}
                                        value={getValue('saveWsdlXml') === 1 ? (getValue('wsdlXml') || '暂无内容') : '暂无内容'}
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
                    </div>
                </section>
            </div>
        )
    }
}
export default Webservice;
