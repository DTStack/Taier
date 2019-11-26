
import * as React from 'react';
import { Select } from 'antd';
import { connect } from 'react-redux';
import { API } from '../../api/apiMap';
import LocalDb from 'utils/localDb';

const Option = Select.Option;
interface Iprops {
    onChange: any;
    value: any;
    user?: any;
    project?: any;
}
interface Istate {
    data: [];
}

@(connect((state: any) => {
    return {
        user: state.user,
        project: state.project
    }
})as any)
export default class SelectEntity extends React.Component<Iprops, Istate> {
    state: Istate = {
        data: []
    }
    componentDidUpdate (preProps) {
        const { user } = this.props;
        if (user.id && user.id != preProps.user.id) {
            this.selectEntity();
        }
    }
    componentDidMount () {
        const { user } = this.props;
        if (user.id) {
            this.selectEntity();
        }
    }
    selectEntity = () => {
        const { user, project } = this.props;
        let userId = user.id;
        let projectId = project.id;
        let hash = window.location.hash;
        const record = `entity-${userId}-${projectId}-${hash}`
        API.selectEntity().then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    data
                });
                let recordValue = LocalDb.get(record);
                if (recordValue) {
                    this.props.onChange(recordValue)
                } else {
                    data[0] && this.props.onChange(data[0].id)
                }
            }
        })
    }
    onChange = (value) => {
        const { user, project } = this.props;
        let userId = user.id;
        let projectId = project.id;
        let hash = window.location.hash;
        const record = `entity-${userId}-${projectId}-${hash}`
        LocalDb.set(record, value);
        this.props.onChange(value)
    }
    render () {
        const { value } = this.props;
        const { data } = this.state;
        return (
            <Select
                showSearch
                style={{ width: 120 }}
                value={ value }
                placeholder="Select a person"
                optionFilterProp="children"
                onChange={this.onChange}
                filterOption={(input, option) => `${option.props.children}`.toLowerCase().indexOf(input.toLowerCase()) >= 0}
            >
                {
                    data.map((item: any) => {
                        return <Option key={item.id} value={item.id}>{item.entityName}</Option>;
                    })
                }

            </Select>
        )
    }
}
