import React from 'react';
import Editor from 'widgets/editor';

class Webservice extends React.Component {
    render () {
        const { getValue } = this.props;
        console.log(this.props)
        return (
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
                </div>
            </section>
        )
    }
}
export default Webservice;
