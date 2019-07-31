import { PureComponent } from 'react'
import '../../styles/views/index/index.scss';
class Container extends React.PureComponent<any, any> {
    render () {
        return this.props.children
    }
}
export default Container
