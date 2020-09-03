import React, { Component } from 'react';
import { Carousel } from 'antd';
import './style.scss'

export default class Swiper extends Component {
    constructor(props) {
        super(props)
      }

      render(){
        let { autoplay=false,dots=true,easing='linear',effect='scrollx',vertical=false} = this.props.config;
        let { data } = this.props;
        return(
            <Carousel {...this.props.config}>
                {
                    data.map((value,index) => {
                        console.log(value.name)
                        if(value.url){
                            return(
                                <div key={value.name}>
                                    <img src={value.url} />
                                </div>
                            )
                        }else{
                            return(
                                <div key={value.name}>
                                    <h3>{value.name}</h3>
                                </div>
                            )
                        }
                        
                    })
                }
            </Carousel>
        )
      }
}