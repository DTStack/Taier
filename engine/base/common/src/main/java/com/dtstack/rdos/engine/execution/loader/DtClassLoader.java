package com.dtstack.rdos.engine.execution.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * 自定义类加载器--->优先从当前加载器获取class
 * Date: 2017/6/18
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class DtClassLoader extends URLClassLoader {

    private static Logger log = LoggerFactory.getLogger(DtClassLoader.class);

    private static final String CLASS_FILE_SUFFIX = ".class";

    protected boolean delegate = false;

    /**
     * The parent class loader.
     */
    protected ClassLoader parent;

    private Map<String, Class<?>> resourceEntries = new HashMap<>();


    public DtClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.parent = parent;
    }

    public DtClassLoader(URL[] urls) {
        super(urls);
    }

    public DtClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (log.isDebugEnabled())
                log.debug("loadClass(" + name + ", " + resolve + ")");
            Class<?> clazz = null;

            // (0) Check our previously loaded local class cache
            clazz = findLoadedClass0(name);
            if (clazz != null) {
                if (log.isDebugEnabled())
                    log.debug("  Returning class from cache");
                if (resolve)
                    resolveClass(clazz);
                return (clazz);
            }

            // (0.1) Check our previously loaded class cache
            clazz = findLoadedClass(name);
            if (clazz != null) {
                if (log.isDebugEnabled())
                    log.debug("  Returning class from cache");
                if (resolve)
                    resolveClass(clazz);
                return (clazz);
            }


            boolean delegateLoad = delegate || filter(name, true);

            // (1) Delegate to our parent if requested
            if (delegateLoad) {
                if (log.isDebugEnabled())
                    log.debug("  Delegating to parent classloader1 " + parent);
                try {
                    clazz = Class.forName(name, false, parent);
                    if (clazz != null) {
                        if (log.isDebugEnabled())
                            log.debug("  Loading class from parent");
                        if (resolve)
                            resolveClass(clazz);
                        return (clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore
                }
            }

            // (2) Search local repositories
            if (log.isDebugEnabled())
                log.debug("  Searching local repositories");
            try {
                clazz = findClass(name);
                if (clazz != null) {
                    if (log.isDebugEnabled())
                        log.debug("  Loading class from local repository");
                    if (resolve)
                        resolveClass(clazz);
                    return (clazz);
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }

            // (3) Delegate to parent unconditionally
            if (!delegateLoad) {
                if (log.isDebugEnabled())
                    log.debug("  Delegating to parent classloader at end: " + parent);
                try {
                    clazz = Class.forName(name, false, parent);
                    if (clazz != null) {
                        if (log.isDebugEnabled())
                            log.debug("  Loading class from parent");
                        if (resolve)
                            resolveClass(clazz);
                        return (clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore
                }
            }
        }

        throw new ClassNotFoundException(name);
    }

    protected Class<?> findLoadedClass0(String name) {

        String path = binaryNameToPath(name, true);

        Class<?> entry = resourceEntries.get(path);
        return entry;
    }

    private String binaryNameToPath(String binaryName, boolean withLeadingSlash) {
        // 1 for leading '/', 6 for ".class"
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }
        path.append(binaryName.replace('.', '/'));
        path.append(CLASS_FILE_SUFFIX);
        return path.toString();
    }


    private String nameToPath(String name) {
        if (name.startsWith("/")) {
            return name;
        }
        StringBuilder path = new StringBuilder(
                1 + name.length());
        path.append('/');
        path.append(name);
        return path.toString();
    }


    /**
     * Returns true if the specified package name is sealed according to the
     * given manifest.
     *
     * @param name Path name to check
     * @param man Associated manifest
     * @return <code>true</code> if the manifest associated says it is sealed
     */
    protected boolean isPackageSealed(String name, Manifest man) {

        String path = name.replace('.', '/') + '/';
        Attributes attr = man.getAttributes(path);
        String sealed = null;
        if (attr != null) {
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        if (sealed == null) {
            if ((attr = man.getMainAttributes()) != null) {
                sealed = attr.getValue(Attributes.Name.SEALED);
            }
        }
        return "true".equalsIgnoreCase(sealed);

    }

    /**
     * Filter classes.
     *
     * @param name class name
     * @param isClassName <code>true</code> if name is a class name,
     *                <code>false</code> if name is a resource name
     * @return <code>true</code> if the class should be filtered
     */
    protected boolean filter(String name, boolean isClassName) {

        if (name == null)
            return false;

        char ch;
        if (name.startsWith("javax")) {
            /* 5 == length("javax") */
            if (name.length() == 5) {
                return false;
            }
            ch = name.charAt(5);
            if (isClassName && ch == '.') {
                /* 6 == length("javax.") */
                if (name.startsWith("servlet.jsp.jstl.", 6)) {
                    return false;
                }
                if (name.startsWith("el.", 6) ||
                        name.startsWith("servlet.", 6) ||
                        name.startsWith("websocket.", 6) ||
                        name.startsWith("security.auth.message.", 6)) {
                    return true;
                }
            } else if (!isClassName && ch == '/') {
                /* 6 == length("javax/") */
                if (name.startsWith("servlet/jsp/jstl/", 6)) {
                    return false;
                }
                if (name.startsWith("el/", 6) ||
                        name.startsWith("servlet/", 6) ||
                        name.startsWith("websocket/", 6) ||
                        name.startsWith("security/auth/message/", 6)) {
                    return true;
                }
            }
        } else if (name.startsWith("org")) {
            /* 3 == length("org") */
            if (name.length() == 3) {
                return false;
            }
            ch = name.charAt(3);
            if (isClassName && ch == '.') {
                /* 4 == length("org.") */
                if (name.startsWith("apache.", 4)) {
                    /* 11 == length("org.apache.") */
                    if (name.startsWith("tomcat.jdbc.", 11)) {
                        return false;
                    }
                    if (name.startsWith("el.", 11) ||
                            name.startsWith("catalina.", 11) ||
                            name.startsWith("jasper.", 11) ||
                            name.startsWith("juli.", 11) ||
                            name.startsWith("tomcat.", 11) ||
                            name.startsWith("naming.", 11) ||
                            name.startsWith("coyote.", 11)) {
                        return true;
                    }
                }
            } else if (!isClassName && ch == '/') {
                /* 4 == length("org/") */
                if (name.startsWith("apache/", 4)) {
                    /* 11 == length("org/apache/") */
                    if (name.startsWith("tomcat/jdbc/", 11)) {
                        return false;
                    }
                    if (name.startsWith("el/", 11) ||
                            name.startsWith("catalina/", 11) ||
                            name.startsWith("jasper/", 11) ||
                            name.startsWith("juli/", 11) ||
                            name.startsWith("tomcat/", 11) ||
                            name.startsWith("naming/", 11) ||
                            name.startsWith("coyote/", 11)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
