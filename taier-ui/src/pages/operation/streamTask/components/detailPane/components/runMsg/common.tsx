import * as React from 'react';
import { Modal } from 'antd';
import LinkDiagram from './linkDiagram';

interface States {
    visible: boolean;
    subTreeData: any[];
    targetKey: string;
}

class Common extends React.Component<any, States> {
    constructor (props: any) {
        super(props);
        this.state = {
            visible: false,
            subTreeData: [],
            targetKey: '' + Math.random() // 绑定 graph id
        }
    }
    showSubVertex = (data: any) => {
        this.setState({ subTreeData: data || [], visible: true })
    }
    render () {
        const { visible, subTreeData, targetKey } = this.state;
        const heightFix = {
            height: '600px'
        }
        return (
            <>
                <LinkDiagram {...this.props} targetKey={targetKey} showSubVertex={this.showSubVertex}/>
                <Modal
                    wrapClassName="vertical-center-modal modal-body-nopadding modal-body--height100"
                    visible={visible}
                    title='工作流'
                    onCancel={() => {
                        this.setState({ visible: false })
                    }}
                    footer={null}
                    zIndex={1000}
                    {...heightFix}
                    width={900}
                >
                    <div id={targetKey} className='graph_wrapper__height'>
                        <LinkDiagram {...this.props} targetKey={targetKey} subTreeData={subTreeData} style={{ height: '100%' }} isSubVertex showSubVertex={this.showSubVertex} />
                    </div>
                </Modal>
            </>
        )
    }
}
export default Common;
