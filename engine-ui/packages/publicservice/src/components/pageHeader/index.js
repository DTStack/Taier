import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Breadcrumb, Button } from 'antd';
import { Link } from "react-router-dom";
import './style.scss';

class PageHeader extends Component {
	constructor(props) {
		super(props);
	}
	render() {
		const {title,content,extra,breadcrumb,crumbItem}=this.props;
		return (
			<div className="pageHeader">
				<div className="pg_main">
					{
						breadcrumb&&(<Breadcrumb className="breadcrumb">
							{
								crumbItem.map((item,index) =>{
									return (
										<Breadcrumb.Item key={index}>
										{
										index !=(crumbItem.length - 1) ? <Link to={item.url}>{item.text}</Link>:item.text
										}
										</Breadcrumb.Item>
									)
								} )
							}
						</Breadcrumb>)
					}
					
					<div className="pg_detail">
						<div className="pg_title">{title}</div>
						<div className="pg_content">{content}</div>
					</div>
				</div>
				{
					extra&&(
						<div className="pg_btn_wrap">
						{
							extra
						}
						 </div>
					)
				}
			</div>
		)
	}
}

PageHeader.defaultProps={
	title:'title',
	content:'content',
	Breadcrumb:false,
	crumbItem:[{text:'home',url:'/app'}],
	extra:null,
}
PageHeader.propTypes = {
	title:PropTypes.string,
	content:PropTypes.string,
	Breadcrumb:PropTypes.bool,
	crumbItem:PropTypes.arrayOf(PropTypes.any),
	extra:PropTypes.any,
}

export default PageHeader