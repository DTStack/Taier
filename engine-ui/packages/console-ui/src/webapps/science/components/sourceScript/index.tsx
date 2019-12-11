import * as React from 'react';
import { Input } from 'antd';

const InputGroup = Input.Group;

class InputSource extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }
    render () {
        const source = Array.from({ length: 4 }).map((ele: any, index: number) => ({ tableName: `table${index}` }));
        const { data } = this.props;
        return (
            <>
                {source.map((item: any, index: number) => {
                    return (
                        <InputGroup compact key={index} style={{ marginTop: -1 }}>
                            <Input style={{ width: '20%', background: '#fff' }} defaultValue={item.tableName} disabled />
                            <Input style={{ width: '80%', background: '#fff' }} disabled value={data.nodeList && data.nodeList[index]} />
                        </InputGroup>
                    )
                })}
            </>
        )
    }
}

export default InputSource
