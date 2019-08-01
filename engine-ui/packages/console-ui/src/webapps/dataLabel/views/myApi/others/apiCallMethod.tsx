import * as React from 'react';
import { Link } from 'react-router';

class ApiCallMethod extends React.Component<any, any> {
    state={
        callUrl: ''
    }
    getApiCallUrl (apiId?: any) {
        apiId = apiId || this.props.showRecord.apiId;
        this.props.getApiCallUrl(apiId)
            .then(
                (res: any) => {
                    if (res) {
                        this.setState({
                            callUrl: res.data
                        })
                    }
                }
            );
    }
    componentDidMount () {
        this.getApiCallUrl();
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        if (nextProps.showRecord && this.props.showRecord.apiId != nextProps.showRecord.apiId) {
            if (nextProps.slidePaneShow) {
                this.getApiCallUrl(nextProps.showRecord.apiId);
            }
        }
    }
    render () {
        const url = '/dl/market/detail/' + this.props.showRecord.apiId;
        return (
            <div style={{ paddingLeft: 30 }}>
                <p style={{ lineHeight: '30px' }}>调用URL：{this.state.callUrl}</p>
                <Link to={url}>在标签市场中查看</Link>
            </div>
        )
    }
}
export default ApiCallMethod;
