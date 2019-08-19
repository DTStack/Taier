import * as React from 'react';

import { Transfer, Button } from 'antd';

class MultipleTableSelect extends React.PureComponent<any, any> {
    state: any = {
        selectKeys: []
    }
    handleChange = (targetKeys: any) => {
        this.setState({ selectKeys: targetKeys });
    }
    onComplete = () => {
        const { selectKeys } = this.state;
        this.setState({
            selectKeys: []
        });
        this.props.onComplete(selectKeys);
    }
    render () {
        const { selectKeys } = this.state;
        const { tableList = [] } = this.props;
        return <div className='c-form__multipleTableSelect'>
            <Transfer
                className='c-form__multipleTableSelect__transfer'
                dataSource={tableList.map((table: any) => {
                    return {
                        key: table,
                        title: table
                    }
                })}
                showSearch
                targetKeys={selectKeys}
                onChange={this.handleChange}
                render={(item: any) => item.title}
                titles={['张表', '张表']}
            />
            <Button type='primary' disabled={!selectKeys.length} className='c-form__multipleTableSelect__add' onClick={this.onComplete} icon='add'>添加一组</Button>
        </div>
    }
}
export default MultipleTableSelect;
