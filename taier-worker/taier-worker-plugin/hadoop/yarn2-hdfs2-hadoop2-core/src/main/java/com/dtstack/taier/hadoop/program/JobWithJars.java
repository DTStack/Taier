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

package com.dtstack.taier.hadoop.program;

import com.dtstack.taier.base.enums.ClassLoaderType;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A JobWithJars is a Flink dataflow plan, together with a bunch of JAR files that contain
 * the classes of the functions and libraries necessary for the execution.
 */
public class JobWithJars {

	private List<URL> jarFiles;

	/**
	 * classpaths that are needed during user code execution.
	 */
	private List<URL> classpaths;

	private ClassLoader userCodeClassLoader;

	private static Map<String, URLClassLoader> cacheClassLoader = new ConcurrentHashMap<>();

	/**
	 * Returns list of jar files that need to be submitted with the plan.
	 */
	public List<URL> getJarFiles() {
		return this.jarFiles;
	}

	/**
	 * Returns list of classpaths that need to be submitted with the plan.
	 */
	public List<URL> getClasspaths() {
		return classpaths;
	}

	/**
	 * Gets the {@link ClassLoader} that must be used to load user code classes.
	 *
	 * @return The user code ClassLoader.
	 */
	public ClassLoader getUserCodeClassLoader() {
		if (this.userCodeClassLoader == null) {
			this.userCodeClassLoader = buildUserCodeClassLoader(jarFiles, classpaths, getClass().getClassLoader(), ClassLoaderType.PARENT_FIRST);
		}
		return this.userCodeClassLoader;
	}

	public static void checkJarFile(URL jar) throws IOException {
		File jarFile;
		try {
			jarFile = new File(jar.toURI());
		} catch (URISyntaxException e) {
			throw new IOException("JAR file path is invalid '" + jar + "'");
		}
		if (!jarFile.exists()) {
			throw new IOException("JAR file does not exist '" + jarFile.getAbsolutePath() + "'");
		}
		if (!jarFile.canRead()) {
			throw new IOException("JAR file can't be read '" + jarFile.getAbsolutePath() + "'");
		}
		// TODO: Check if proper JAR file
	}

	public static ClassLoader buildUserCodeClassLoader(List<URL> jars, List<URL> classpaths, ClassLoader parent, ClassLoaderType classLoaderType) {
		if (ClassLoaderType.NONE == classLoaderType) {
			return parent;
		}
		URL[] urls = new URL[jars.size() + classpaths.size()];
		for (int i = 0; i < jars.size(); i++) {
			urls[i] = jars.get(i);
		}
		for (int i = 0; i < classpaths.size(); i++) {
			urls[i + jars.size()] = classpaths.get(i);
		}
		switch (classLoaderType) {
			case CHILD_FIRST_CACHE:
				Arrays.sort(urls, Comparator.comparing(URL::toString));
				String jarsKeyChild = StringUtils.join(urls, "_");
				return cacheClassLoader.computeIfAbsent(jarsKeyChild, k -> UserCodeClassLoaders.childFirst(urls, parent, new String[]{}));
			case PARENT_FIRST_CACHE:
				Arrays.sort(urls, Comparator.comparing(URL::toString));
				String jarsKeyParent = StringUtils.join(urls, "_");
				return cacheClassLoader.computeIfAbsent(jarsKeyParent, k -> UserCodeClassLoaders.parentFirst(urls, parent));
			case CHILD_FIRST:
				return UserCodeClassLoaders.childFirst(urls, parent, new String[]{});
			case PARENT_FIRST:
				return UserCodeClassLoaders.parentFirst(urls, parent);
			default:
				return UserCodeClassLoaders.parentFirst(urls, parent);
		}
	}
}
