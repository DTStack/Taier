//package com.dtstack.task.web;
//
//import com.dtstack.dtcenter.common.login.SessionUtil;
//import com.dtstack.engine.common.Service;
//import com.dtstack.engine.common.env.EnvironmentContext;
//import com.dtstack.task.web.verticles.ServerVerticle;
//import io.vertx.core.DeploymentOptions;
//import io.vertx.core.Vertx;
//import io.vertx.core.VertxOptions;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//
///**
// * @author sishu.yss
// */
//public class WebService implements Service {
//
//    private static Logger logger = LoggerFactory.getLogger(WebService.class);
//
//    private ApplicationContext context;
//
//    private EnvironmentContext environmentContext;
//
//    private Vertx vertx;
//
//
//    public WebService(ApplicationContext context) {
//        this.context = context;
//        this.environmentContext = (EnvironmentContext) context.getBean("environmentContext");
//    }
//
//    /**
//     * 登陆统一用uic
//     */
//    @Override
//    public void initService() {
//        logger.info("WebService start...");
//        VertxOptions options = new VertxOptions();
//        //设置Vert.x实例使用的Event Loop线程的数量
//        options.setEventLoopPoolSize(environmentContext.getEventLoopPoolSize());
//        //Worker线程的最大执行时间
//        options.setMaxWorkerExecuteTime(environmentContext.getMaxWorkerExecuteTime());
//        vertx = Vertx.vertx(options);
//        DeploymentOptions deploymentOptions = new DeploymentOptions();
//        deploymentOptions.setInstances(environmentContext.getInstances());
//        deploymentOptions.setWorker(true);
//        //设置Vert.x实例中支持的Worker线程的最大数量，默认值为20
//        deploymentOptions.setWorkerPoolSize(environmentContext.getWorkerPoolSize());
//        ServerVerticle.setContext(context);
//        ServerVerticle.setEnvironmentContext(environmentContext);
//        SessionUtil.setContext(context);
//        vertx.deployVerticle(ServerVerticle.class.getName(), deploymentOptions);
//    }
//
//    @Override
//    public void close() {
//        if (vertx != null) {
//            vertx.close();
//        }
//    }
//
//}
