//package com.dtstack.engine.master.router.vertx;
//
//
//import com.dtstack.engine.master.router.callback.ApiCallback;
//import com.dtstack.engine.master.router.callback.ApiCallbackMethod;
//import com.dtstack.engine.master.router.util.RequestUtil;
//import io.vertx.ext.web.RoutingContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//
//
///**
// * @author sishu.yss
// */
//public class AllRequestVerticle extends BaseVerticle {
//
//	private static Logger logger = LoggerFactory.getLogger(AllRequestVerticle.class);
//
//    public AllRequestVerticle(ApplicationContext context) {
//        if (this.context == null) {
//            this.context = context;
//        }
//    }
//
//    public void request(final RoutingContext routingContext) {
//
//        final AllRequestVerticle allRequestVerticle = this;
//
//        ApiCallbackMethod.doCallback(new ApiCallback() {
//
//            @Override
//            public Object execute() throws Exception {
//                return allRequestVerticle.reflectionMethod(routingContext, RequestUtil.getRequestParams(context, routingContext));
//            }
//
//        }, routingContext);
//    }
//}
