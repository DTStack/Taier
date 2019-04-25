import { PureComponent } from 'react'
import '../../styles/views/index/index.scss';
class Container extends PureComponent {
    render () {
        return this.props.children
    }
}
export default Container
