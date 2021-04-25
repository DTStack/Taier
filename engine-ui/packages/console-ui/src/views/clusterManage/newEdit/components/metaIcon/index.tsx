import * as React from 'react'

interface IProps {
    comp: any;
    isMetadata: undefined | boolean;
}

export default class MetaIcon extends React.PureComponent<IProps, any> {
    render () {
        const { comp, isMetadata } = this.props

        if (isMetadata ?? comp.multiVersion[0]?.isMetadata) {
            return <span className="c-metaIcon__title">Meta</span>
        }
        return null
    }
}
