package com.dtstack.engine.master.router.vertx;

import com.dtstack.engine.common.RootUrls;
import com.dtstack.engine.master.env.EnvironmentContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sishu.yss
 */

public class ServerVerticle extends AbstractVerticle{

    private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

    private static ApplicationContext context;

    private static EnvironmentContext environmentContext;

    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHearders())
                .allowedMethods(allowMethods()));
        router.route().handler(CookieHandler.create());
        LoginVerticle loginVerticle = new LoginVerticle(context);
        ResourceVerticle resourceVerticle = new ResourceVerticle(context);

        router.post(RootUrls.ROOT  + "/login/out").handler(loginVerticle::loginOut);
        router.postWithRegex(RootUrls.ROOT + "/upload/.*").handler(resourceVerticle::handleUploadResource);
        router.post(RootUrls.ROOT + "/*").handler(loginVerticle::handleLogin);
        router.postWithRegex(RootUrls.ROOT + "/.*").handler(new AllRequestVerticle(context)::request);
        router.getWithRegex(RootUrls.ROOT + "/download/component/downloadKerberosXML").handler(resourceVerticle::handleDownloadXml);

        vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true))
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", environmentContext.getHttpPort()),
                        config().getString("http.address", environmentContext.getHttpAddress()), result -> {
                            if (result.succeeded()){
                                future.complete();
                            }else{
                                future.fail(result.cause());
                                LOG.error("http.address:{} http.port:{}", environmentContext.getHttpAddress(), environmentContext.getHttpPort(), result.cause());
                                System.exit(-1);
                            }
                        });
    }

    private Set<String> allowHearders() {
        Set<String> allowHeaders = new HashSet<String>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        return allowHeaders;
    }

    private Set<HttpMethod> allowMethods() {
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.POST);
        return allowMethods;
    }

    public static void setContext(ApplicationContext context) {
        ServerVerticle.context = context;
    }

    public static void setEnvironmentContext(EnvironmentContext environmentContext) {
        ServerVerticle.environmentContext = environmentContext;
    }

}
