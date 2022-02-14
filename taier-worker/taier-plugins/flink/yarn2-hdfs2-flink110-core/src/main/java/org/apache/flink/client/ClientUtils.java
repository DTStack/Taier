/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.client;

import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.flink.constrant.ConfigConstrant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ContextEnvironment;
import org.apache.flink.client.program.ContextEnvironmentFactory;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.core.execution.DetachedJobExecutionResult;
import org.apache.flink.core.execution.PipelineExecutorServiceLoader;
import org.apache.flink.runtime.client.JobExecutionException;
import org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.shaded.guava18.com.google.common.base.Splitter;
import org.apache.flink.shaded.guava18.com.google.common.collect.Iterables;
import org.apache.flink.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * Utility functions for Flink client.
 */
public enum ClientUtils {
	;

	private static final Logger LOG = LoggerFactory.getLogger(ClientUtils.class);

	private static Map<String, URLClassLoader> cacheClassLoader = new ConcurrentHashMap<>();

	public static ClassLoader buildUserCodeClassLoader(
			List<URL> jars,
			List<URL> classpaths,
			ClassLoader parent,
			Configuration configuration) {
		return buildUserCodeClassLoader(jars, classpaths, parent, configuration, false);
	}

	public static ClassLoader buildUserCodeClassLoader(
			List<URL> jars,
			List<URL> classpaths,
			ClassLoader parent,
			Configuration configuration,
			boolean cache) {
		URL[] urls = new URL[jars.size() + classpaths.size()];

		for (int i = 0; i < jars.size(); i++) {
			urls[i] = jars.get(i);
		}
		for (int i = 0; i < classpaths.size(); i++) {
			urls[i + jars.size()] = classpaths.get(i);
		}
		final String[] alwaysParentFirstLoaderPatterns = CoreOptions.getParentFirstLoaderPatterns(configuration);

		final String[] childFirstLoaderPatternsArray = parseChildFirstLoaderPatterns(configuration);
		final List<String> childFirstLoaderPatterns = Arrays.asList(childFirstLoaderPatternsArray);

		final String classLoaderResolveOrder =
				configuration.getString(CoreOptions.CLASSLOADER_RESOLVE_ORDER);
		FlinkUserCodeClassLoaders.ResolveOrder resolveOrder =
				FlinkUserCodeClassLoaders.ResolveOrder.fromString(classLoaderResolveOrder);
		final URLClassLoader classLoader = FlinkUserCodeClassLoaders.create(
				resolveOrder, urls, parent, alwaysParentFirstLoaderPatterns, childFirstLoaderPatterns);
		if (cache) {
			Arrays.sort(urls, Comparator.comparing(URL::toString));
			String[] jarMd5s = new String[urls.length];
			for (int i = 0; i < urls.length; ++i) {
				try (FileInputStream inputStream = new FileInputStream(urls[i].getPath())){
					jarMd5s[i] = DigestUtils.md5Hex(inputStream);
				} catch (Exception e) {
					throw new PluginDefineException("Exceptions appears when read file:" + e);
				}
			}
			String keyCache = classLoaderResolveOrder + StringUtils.join(jarMd5s, "_");
			return cacheClassLoader.computeIfAbsent(keyCache, k -> classLoader);
		} else {
			return classLoader;
		}
	}

	private static String[] parseChildFirstLoaderPatterns(Configuration configuration) {
		Splitter splitter = Splitter.on(';').omitEmptyStrings();
		final String childFirstPatternsStr = configuration.getString(ConfigConstrant.CHILD_FIRST_LOADER_PATTERNS,
				ConfigConstrant.CHILD_FIRST_LOADER_PATTERNS_DEFAULT);
		return Iterables.toArray(splitter.split(childFirstPatternsStr), String.class);
	}

	public static JobExecutionResult submitJob(
			ClusterClient<?> client,
			JobGraph jobGraph) throws ProgramInvocationException {
		checkNotNull(client);
		checkNotNull(jobGraph);
		try {
			return client
				.submitJob(jobGraph)
				.thenApply(DetachedJobExecutionResult::new)
				.get();
		} catch (InterruptedException | ExecutionException e) {
			ExceptionUtils.checkInterrupted(e);
			throw new ProgramInvocationException("Could not run job in detached mode.", jobGraph.getJobID(), e);
		}
	}

	public static JobExecutionResult submitJob(
			ClusterClient<?> client,
			JobGraph jobGraph, long timeout, TimeUnit unit) throws ProgramInvocationException, TimeoutException {
		checkNotNull(client);
		checkNotNull(jobGraph);
		try {
			return client
					.submitJob(jobGraph)
					.thenApply(DetachedJobExecutionResult::new)
					.get(timeout, unit);
		} catch (InterruptedException | ExecutionException e) {
			ExceptionUtils.checkInterrupted(e);
			throw new ProgramInvocationException("Could not run job in detached mode.", jobGraph.getJobID(), e);
		}
	}

	public static JobExecutionResult submitJobAndWaitForResult(
			ClusterClient<?> client,
			JobGraph jobGraph,
			ClassLoader classLoader) throws ProgramInvocationException {
		checkNotNull(client);
		checkNotNull(jobGraph);
		checkNotNull(classLoader);

		JobResult jobResult;

		try {
			jobResult = client
				.submitJob(jobGraph)
				.thenCompose(client::requestJobResult)
				.get();
		} catch (InterruptedException | ExecutionException e) {
			ExceptionUtils.checkInterrupted(e);
			throw new ProgramInvocationException("Could not run job", jobGraph.getJobID(), e);
		}

		try {
			return jobResult.toJobExecutionResult(classLoader);
		} catch (JobExecutionException | IOException | ClassNotFoundException e) {
			throw new ProgramInvocationException("Job failed", jobGraph.getJobID(), e);
		}
	}

	public static void executeProgram(
			PipelineExecutorServiceLoader executorServiceLoader,
			Configuration configuration,
			PackagedProgram program) throws ProgramInvocationException {
		checkNotNull(executorServiceLoader);
		final ClassLoader userCodeClassLoader = program.getUserCodeClassLoader();
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(userCodeClassLoader);

			LOG.info("Starting program (detached: {})", !configuration.getBoolean(DeploymentOptions.ATTACHED));

			ContextEnvironmentFactory factory = new ContextEnvironmentFactory(
					executorServiceLoader,
					configuration,
					userCodeClassLoader);
			ContextEnvironment.setAsContext(factory);

			try {
				program.invokeInteractiveModeForExecution();
			} finally {
				ContextEnvironment.unsetContext();
			}
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}
}
