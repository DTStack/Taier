import React from "react"

class BaseInfo extends React.Component {
    componentDidMount(){
        console.log("BaseInfo")
    }
   
    render() {
        return (
           <div>
            BaseInfo
           </div>
        )
    }
}

export default BaseInfo;