import * as React from 'react';
import styled from 'styled-components';

const Container = styled.div`
    height: 267px;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
`;

const Icon = styled.img`
    width: 85px;
    height: 85px;
    margin-right: 32px;
`;

const Dot = styled.span`
    background: #2491F7;
    width: 6px;
    height: 6px;
    margin: 12px;
    display: inline-block;
    margin-right: 4px;
    float: left;
    border-radius: 50% 50%;
`

const Li = styled.p`
    font-size: 14px;
    color: #666666;
    line-height: 30px;
    height: 30px;
    vertical-align: middle;
`


export default function EmptyGuide () {
    return (
        <Container>
            <Icon src="/public/tagEngine/img/relation-guide.png" />
            <div>
                <Li><Dot />选择实体</Li>
                <Li><Dot />配置公共主键</Li>
                <Li><Dot />建立关系模型</Li>
                <Li><Dot />丰富标签体系建设的数据维度</Li>
            </div>
        </Container>
    )
}
