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

package com.dtstack.engine.hadoop.program;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.worker.enums.ClassLoaderType;
import org.apache.hadoop.conf.Configuration;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * This class encapsulates represents a program, packaged in a jar file. It supplies
 * functionality to extract nested libraries, search for the program entry point, and extract
 * a program plan.
 */
public class PackagedProgram {

	/**
	 * Property name of the entry in JAR manifest file that describes the Flink specific entry point.
	 */
	public static final String MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS = "program-class";

	/**
	 * Property name of the entry in JAR manifest file that describes the class with the main method.
	 */
	public static final String MANIFEST_ATTRIBUTE_MAIN_CLASS = "Main-Class";

	// --------------------------------------------------------------------------------------------

	private final URL jarFile;

	private final String[] args;

	private final Class<?> mainClass;

	private final List<File> extractedTempLibraries;

	private final List<URL> classpaths;

	private ClassLoader userCodeClassLoader;

	/**
	 * Creates an instance that wraps the plan defined in the jar file using the given
	 * arguments. For generating the plan the class defined in the className parameter
	 * is used.
	 *
	 * @param jarFile
	 *        The jar file which contains the plan.
	 * @param classpaths
	 *        Additional classpath URLs needed by the Program.
	 * @param entryPointClassName
	 *        Name of the class which generates the plan. Overrides the class defined
	 *        in the jar file manifest
	 * @param args
	 *        Optional. The arguments used to create the pact plan, depend on
	 *        implementation of the pact plan. See getDescription().
	 * @throws RdosDefineException
	 *         This invocation is thrown if the Program can't be properly loaded. Causes
	 *         may be a missing / wrong class or manifest files.
	 */
	public PackagedProgram(File jarFile, List<URL> classpaths, ClassLoaderType classLoaderType, @Nullable String entryPointClassName, String... args) throws RdosDefineException {
		if (jarFile == null) {
			throw new IllegalArgumentException("The jar file must not be null.");
		}

		URL jarFileUrl;
		try {
			jarFileUrl = jarFile.getAbsoluteFile().toURI().toURL();
		} catch (MalformedURLException e1) {
			throw new IllegalArgumentException("The jar file path is invalid.");
		}

		checkJarFile(jarFileUrl);

		this.jarFile = jarFileUrl;
		this.args = args == null ? new String[0] : args;

		// if no entryPointClassName name was given, we try and look one up through the manifest
		if (entryPointClassName == null) {
			entryPointClassName = getEntryPointClassNameFromJar(jarFileUrl);
		}

		// now that we have an entry point, we can extract the nested jar files (if any)
		this.extractedTempLibraries = extractContainedLibraries(jarFileUrl);
		this.classpaths = classpaths;
		this.userCodeClassLoader = JobWithJars.buildUserCodeClassLoader(getAllLibraries(), classpaths, getClass().getClassLoader(), classLoaderType);

		// load the entry point class
		this.mainClass = loadMainClass(entryPointClassName, userCodeClassLoader);

		hasMainMethod(mainClass);
	}

	public String[] getArguments() {
		return this.args;
	}

	public String getMainClassName() {
		return this.mainClass.getName();
	}

	/**
	 * This method assumes that the context environment is prepared, or the execution
	 * will be a local execution by default.
	 */
	public String invokeInteractiveModeForExecution(Configuration configuration) throws RdosDefineException{
		return callMainMethod(mainClass, configuration, args);
	}

	/**
	 * Returns the classpaths that are required by the program.
	 *
	 * @return List of {@link URL}s.
	 */
	public List<URL> getClasspaths() {
		return this.classpaths;
	}

	/**
	 * Gets the {@link ClassLoader} that must be used to load user code classes.
	 *
	 * @return The user code ClassLoader.
	 */
	public ClassLoader getUserCodeClassLoader() {
		return this.userCodeClassLoader;
	}

	/**
	 * Returns all provided libraries needed to run the program.
	 */
	public List<URL> getAllLibraries() {
		List<URL> libs = new ArrayList<URL>(this.extractedTempLibraries.size() + 1);

		if (jarFile != null) {
			libs.add(jarFile);
		}
		for (File tmpLib : this.extractedTempLibraries) {
			try {
				libs.add(tmpLib.getAbsoluteFile().toURI().toURL());
			}
			catch (MalformedURLException e) {
				throw new RuntimeException("URL is invalid. This should not happen.", e);
			}
		}

		return libs;
	}

	/**
	 * Deletes all temporary files created for contained packaged libraries.
	 */
	public void deleteExtractedLibraries() {
		deleteExtractedLibraries(this.extractedTempLibraries);
		this.extractedTempLibraries.clear();
	}

	private static boolean hasMainMethod(Class<?> entryClass) {
		Method mainMethod;
		try {
			mainMethod = entryClass.getMethod("main", Configuration.class, String[].class);
		} catch (NoSuchMethodException e) {
			return false;
		}
		catch (Throwable t) {
			throw new RuntimeException("Could not look up the main(Configuration,String[]) method from the class " +
					entryClass.getName() + ": " + t.getMessage(), t);
		}

		return Modifier.isStatic(mainMethod.getModifiers()) && Modifier.isPublic(mainMethod.getModifiers());
	}

	private static String callMainMethod(Class<?> entryClass, Configuration configuration, String[] args) throws RdosDefineException {
		Method mainMethod;
		if (!Modifier.isPublic(entryClass.getModifiers())) {
			throw new RdosDefineException("The class " + entryClass.getName() + " must be public.");
		}

		try {
			mainMethod = entryClass.getMethod("main", Configuration.class, String[].class);
		} catch (NoSuchMethodException e) {
			throw new RdosDefineException("The class " + entryClass.getName() + " has no main(String[]) method.");
		}
		catch (Throwable t) {
			throw new RdosDefineException("Could not look up the main(String[]) method from the class " +
					entryClass.getName() + ": " + t.getMessage(), ErrorCode.FUNCTION_CAN_NOT_FIND,t);
		}

		if (!Modifier.isStatic(mainMethod.getModifiers())) {
			throw new RdosDefineException("The class " + entryClass.getName() + " declares a non-static main method.");
		}
		if (!Modifier.isPublic(mainMethod.getModifiers())) {
			throw new RdosDefineException("The class " + entryClass.getName() + " declares a non-public main method.");
		}

		try {
			return (String) mainMethod.invoke(null, configuration, (Object) args);
		}
		catch (IllegalArgumentException e) {
			throw new RdosDefineException("Could not invoke the main method, arguments are not matching.", e);
		}
		catch (IllegalAccessException e) {
			throw new RdosDefineException("Access to the main method was denied: " + e.getMessage(), e);
		}
		catch (InvocationTargetException e) {
			throw new RdosDefineException("The main method caused an error: " + e.getTargetException().getMessage());
		}
		catch (Throwable t) {
			throw new RdosDefineException("An error occurred while invoking the program's main method: " + t.getMessage(), t);
		}
	}

	private static String getEntryPointClassNameFromJar(URL jarFile) throws RdosDefineException {
		JarFile jar;
		Manifest manifest;
		String className;

		// Open jar file
		try {
			jar = new JarFile(new File(jarFile.toURI()));
		} catch (URISyntaxException use) {
			throw new RdosDefineException("Invalid file path '" + jarFile.getPath() + "'", use);
		} catch (IOException ioex) {
			throw new RdosDefineException("Error while opening jar file '" + jarFile.getPath() + "'. "
				+ ioex.getMessage(), ioex);
		}

		// jar file must be closed at the end
		try {
			// Read from jar manifest
			try {
				manifest = jar.getManifest();
			} catch (IOException ioex) {
				throw new RdosDefineException("The Manifest in the jar file could not be accessed '"
					+ jarFile.getPath() + "'. " + ioex.getMessage(), ioex);
			}

			if (manifest == null) {
				throw new RdosDefineException("No manifest found in jar file '" + jarFile.getPath() + "'. The manifest is need to point to the program's main class.");
			}

			Attributes attributes = manifest.getMainAttributes();

			// check for a "program-class" entry first
			className = attributes.getValue(PackagedProgram.MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS);
			if (className != null) {
				return className;
			}

			// check for a main class
			className = attributes.getValue(PackagedProgram.MANIFEST_ATTRIBUTE_MAIN_CLASS);
			if (className != null) {
				return className;
			} else {
				throw new RdosDefineException("Neither a '" + MANIFEST_ATTRIBUTE_MAIN_CLASS + "', nor a '" +
						MANIFEST_ATTRIBUTE_ASSEMBLER_CLASS + "' entry was found in the jar file.");
			}
		}
		finally {
			try {
				jar.close();
			} catch (Throwable t) {
				throw new RdosDefineException("Could not close the JAR file: " + t.getMessage(), t);
			}
		}
	}

	private static Class<?> loadMainClass(String className, ClassLoader cl) throws RdosDefineException {
		ClassLoader contextCl = null;
		try {
			contextCl = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			return Class.forName(className, false, cl);
		}
		catch (ClassNotFoundException e) {
			throw new RdosDefineException("The program's entry point class '" + className
				+ "' was not found in the jar file.", e);
		}
		catch (ExceptionInInitializerError e) {
			throw new RdosDefineException("The program's entry point class '" + className
				+ "' threw an error during initialization.", e);
		}
		catch (LinkageError e) {
			throw new RdosDefineException("The program's entry point class '" + className
				+ "' could not be loaded due to a linkage failure.", e);
		}
		catch (Throwable t) {
			throw new RdosDefineException("The program's entry point class '" + className
				+ "' caused an exception during initialization: " + t.getMessage(), t);
		} finally {
			if (contextCl != null) {
				Thread.currentThread().setContextClassLoader(contextCl);
			}
		}
	}

	/**
	 * Takes all JAR files that are contained in this program's JAR file and extracts them
	 * to the system's temp directory.
	 *
	 * @return The file names of the extracted temporary files.
	 * @throws RdosDefineException Thrown, if the extraction process failed.
	 */
	public static List<File> extractContainedLibraries(URL jarFile) throws RdosDefineException {

		Random rnd = new Random();

		JarFile jar = null;
		try {
			jar = new JarFile(new File(jarFile.toURI()));
			final List<JarEntry> containedJarFileEntries = new ArrayList<JarEntry>();

			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();

				if (name.length() > 8 && name.startsWith("lib/") && name.endsWith(".jar")) {
					containedJarFileEntries.add(entry);
				}
			}

			if (containedJarFileEntries.isEmpty()) {
				return Collections.emptyList();
			}
			else {
				// go over all contained jar files
				final List<File> extractedTempLibraries = new ArrayList<File>(containedJarFileEntries.size());
				final byte[] buffer = new byte[4096];

				boolean incomplete = true;

				try {
					for (int i = 0; i < containedJarFileEntries.size(); i++) {
						final JarEntry entry = containedJarFileEntries.get(i);
						String name = entry.getName();
						// '/' as in case of zip, jar
						// java.util.zip.ZipEntry#isDirectory always looks only for '/' not for File.separator
						name = name.replace('/', '_');

						File tempFile;
						try {
							tempFile = File.createTempFile(rnd.nextInt(Integer.MAX_VALUE) + "_", name);
							tempFile.deleteOnExit();
						}
						catch (IOException e) {
							throw new RdosDefineException(
								"An I/O error occurred while creating temporary file to extract nested library '" +
										entry.getName() + "'.", e);
						}

						extractedTempLibraries.add(tempFile);

						// copy the temp file contents to a temporary File
						OutputStream out = null;
						InputStream in = null;
						try {

							out = new FileOutputStream(tempFile);
							in = new BufferedInputStream(jar.getInputStream(entry));

							int numRead = 0;
							while ((numRead = in.read(buffer)) != -1) {
								out.write(buffer, 0, numRead);
							}
						}
						catch (IOException e) {
							throw new RdosDefineException("An I/O error occurred while extracting nested library '"
									+ entry.getName() + "' to temporary file '" + tempFile.getAbsolutePath() + "'.");
						}
						finally {
							if (out != null) {
								out.close();
							}
							if (in != null) {
								in.close();
							}
						}
					}

					incomplete = false;
				}
				finally {
					if (incomplete) {
						deleteExtractedLibraries(extractedTempLibraries);
					}
				}

				return extractedTempLibraries;
			}
		}
		catch (Throwable t) {
			throw new RdosDefineException("Unknown I/O error while extracting contained jar files.", t);
		}
		finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (Throwable t) {}
			}
		}
	}

	public static void deleteExtractedLibraries(List<File> tempLibraries) {
		for (File f : tempLibraries) {
			f.delete();
		}
	}

	private static void checkJarFile(URL jarfile) throws RdosDefineException {
		try {
			JobWithJars.checkJarFile(jarfile);
		}
		catch (IOException e) {
			throw new RdosDefineException(e.getMessage());
		}
		catch (Throwable t) {
			throw new RdosDefineException("Cannot access jar file" + (t.getMessage() == null ? "." : ": " + t.getMessage()), t);
		}
	}

}
