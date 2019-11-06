import * as React from 'react';
import GoBack from 'widgets/go-back'

import './style.scss'

export default function TitleBar (props: any) {
    const { title, goBack } = props
    return (
        <header className="title-bar bd-bottom">
            <span className="left">{title}</span>&nbsp;&nbsp;
            <GoBack url={goBack} className="right" icon="rollback" size="small" />
        </header>
    )
}
