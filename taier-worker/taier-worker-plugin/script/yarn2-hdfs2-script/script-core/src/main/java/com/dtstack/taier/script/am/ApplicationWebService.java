/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.script.am;

import com.dtstack.taier.script.api.ApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.mapreduce.v2.app.webapp.AMWebApp;
import org.apache.hadoop.service.AbstractService;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.apache.hadoop.yarn.webapp.WebAppException;
import org.apache.hadoop.yarn.webapp.WebApps;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.lang.reflect.Method;

public class ApplicationWebService extends AbstractService {
  private static final Log LOG = LogFactory.getLog(ApplicationWebService.class);
  private WebApp webApp;
  private final ApplicationContext applicationContext;

  public ApplicationWebService(ApplicationContext applicationContext, Configuration conf) {
    super(ApplicationWebService.class.getSimpleName());
    this.setConfig(conf);
    this.applicationContext = applicationContext;
  }

  @Override
  public void start() {
    LOG.info("Starting application web server");
    try {
      Method webAppBuild = WebApps.Builder.class.getMethod("build", WebApp.class);
      webApp = (WebApp) webAppBuild.invoke(WebApps.$for("proxy", ApplicationContext.class, applicationContext, null).with(getConfig()), new AMWebApp());
      HttpServer2 httpServer = webApp.httpServer();

      WebAppContext webAppContext = httpServer.getWebAppContext();
      WebAppContext appWebAppContext = new WebAppContext();
      appWebAppContext.setContextPath("/static/xlWebApp");
      String appDir = getClass().getClassLoader().getResource("xlWebApp").toString();
      appWebAppContext.setResourceBase(appDir);
      appWebAppContext.addServlet(DefaultServlet.class, "/*");
      final String[] ALL_URLS = {"/*"};
      FilterHolder[] filterHolders =
          webAppContext.getServletHandler().getFilters();
      for (FilterHolder filterHolder : filterHolders) {
        if (!"guice".equals(filterHolder.getName())) {
          HttpServer2.defineFilter(appWebAppContext, filterHolder.getName(),
              filterHolder.getClassName(), filterHolder.getInitParameters(),
              ALL_URLS);
        }
      }
      httpServer.addContext(appWebAppContext, true);
      try {
        httpServer.start();
        LOG.info("Web app " + webApp.name() + " started at "
            + httpServer.getConnectorAddress(0).getPort());
      } catch (IOException e) {
        throw new WebAppException("Error starting http server", e);
      }
    } catch (NoSuchMethodException e) {
      LOG.debug("current hadoop version don't have the method build of Class " + WebApps.class.toString() + ". For More Detail: " + e);
      webApp = WebApps.$for("proxy", ApplicationContext.class, applicationContext, null).with(getConfig()).start(new AMWebApp());
    } catch (Exception e) {
      LOG.error("Error starting application web server!", e);
    }
  }

  public int getHttpPort() {
    return webApp.port();
  }
}
