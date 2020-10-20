package com.dtstack.engine.alert.groovy;//package com.dtstack.engine.alert.groovy;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class GroovyScriptService {
//
//	@Autowired
//	private AlertGroovyDao alertGroovyDao;
//
//	public String getGroovyFunc(){
//		StringBuffer sb = new StringBuffer();
//		AlertGroovyPO query = new AlertGroovyPO();
//		query.setName("dt_g_%");
//		List<AlertGroovyPO> groovys = alertGroovyDao.list(query);
//		if(groovys!= null && !groovys.isEmpty()){
//			groovys.forEach(x->{
//				sb.append(x.getBody()).append("\r\n");
//			});
//		}
//		return sb.toString();
//	}
//
//}
