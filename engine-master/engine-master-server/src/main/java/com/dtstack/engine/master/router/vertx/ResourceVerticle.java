package com.dtstack.engine.master.router.vertx;

import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.callback.ApiCallback;
import com.dtstack.engine.master.router.callback.ApiCallbackMethod;
import com.dtstack.engine.master.router.util.RequestUtil;
import com.google.common.collect.Maps;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferFactoryImpl;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * @author toutian
 */
public class ResourceVerticle extends BaseVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ResourceVerticle.class);
    //添加bom头信息
    private static byte[] bomByte = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};


    public ResourceVerticle(ApplicationContext context) {
        this.context = context;
    }

    public void handleDownloadFile(RoutingContext routingContext){
        this.handleDownload(routingContext);
    }

    /**
     * 下载文件
     *
     * @param routingContext
     */
    public void handleDownload(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/octet-stream;charset=UTF-8");
        response.putHeader("Pragma", "no-cache");
        response.putHeader("Cache-Control", "no-cache");
        Map<String, Object> paramMap = Maps.newHashMap();
        MultiMap reqParam = routingContext.request().params();
        reqParam.forEach(entry -> paramMap.put(entry.getKey(), entry.getValue()));
        File downLoadFile = null;
        try {
            Map<String, Object> params = RequestUtil.getRequestParams(paramMap, routingContext);
            downLoadFile = (File) reflectionMethod(routingContext, params);
            if (Objects.isNull(downLoadFile) || downLoadFile.isDirectory()) {
                response.putHeader("Content-Disposition", "attachment;filename=error.log");
                response.write("文件不存在");
            } else {
                response.putHeader("Content-Disposition", "attachment;filename=" + encodeURIComponent(downLoadFile.getName()));
                response.sendFile(downLoadFile.getPath());
            }
        } catch (InvocationTargetException e) {
            response.write("下载文件异常:" + e.getTargetException().getMessage());
        } catch (Exception e) {
            logger.error("", e);
            response.write("下载文件异常:" + e.getMessage());
        } finally {
            if(Objects.nonNull(downLoadFile)){
                downLoadFile.delete();
            }
        }
        routingContext.response().end();
    }

    public static String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (Exception e) {
        }
        return value;
    }

    public void handleUploadResource(RoutingContext routingContext) {

        final ResourceVerticle resourceVerticle = this;
        Set<FileUpload> fileUploadSet = routingContext.fileUploads();

        List<Resource> resources = new ArrayList<>(fileUploadSet.size());
        for (FileUpload fileUpload : fileUploadSet) {
            resources.add(new Resource(fileUpload.fileName(), fileUpload.uploadedFileName(), (int) fileUpload.size(), fileUpload.contentType(), fileUpload.name()));
        }

        ApiCallbackMethod.doCallback(new ApiCallback() {

            @Override
            public Object execute() throws Exception {
                Map<String, Object> paramMap = new HashedMap();
                for (Map.Entry<String, String> tmp : routingContext.request().params()) {
                    paramMap.put(tmp.getKey(), tmp.getValue());
                }

                paramMap.put("resources", resources);

                return resourceVerticle.reflectionMethod(routingContext, RequestUtil.getRequestParams(paramMap, routingContext));
            }

        }, routingContext);
    }

}


