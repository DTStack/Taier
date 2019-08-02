import * as React from 'react'

export default class Resize extends React.Component<any, any> {
    componentDidMount () {
        window.addEventListener('resize', this.resize, false)
    }

    componentWillUnmount () {
        window.removeEventListener('resize', this.resize, false);
    }

    resize = () => {
        console.log('Resize!')
        const { onResize } = this.props;
        if (onResize) onResize()
    }

    render () {
        return this.props.children
    }
}
