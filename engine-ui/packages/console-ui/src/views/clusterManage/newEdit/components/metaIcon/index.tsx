import * as React from 'react'

interface IProps {
    comp: any;
    form: any;
}

export default class MetaIcon extends React.PureComponent<IProps, any> {
    render () {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''

        if (form.getFieldValue(`${typeCode}.isMetadata`) ?? comp.isMetadata) {
            return <span className="c-metaIcon__title">Meta</span>
        }
        return null
    }
}
