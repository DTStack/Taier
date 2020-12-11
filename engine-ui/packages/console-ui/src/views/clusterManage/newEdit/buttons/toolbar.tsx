import * as React from 'react'
import { Popconfirm, Button } from 'antd'

export default class ToolBar extends React.PureComponent<any, any> {
    render () {
        return (
            <div className="c-toolbar__container">
                <Popconfirm
                    title="确认取消当前更改？"
                    okText="确认"
                    cancelText="取消"
                >
                    <Button style={{ width: 88 }}>取消</Button>
                </Popconfirm>
                <Button type="primary" style={{ marginLeft: 8, width: 88 }} >保存</Button>
            </div>
        )
    }
}
