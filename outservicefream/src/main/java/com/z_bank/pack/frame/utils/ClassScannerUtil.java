package com.z_bank.pack.frame.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

public class ClassScannerUtil {

	private static Logger log = Logger.getLogger(ClassScannerUtil.class);
	
	public static void main(String[] argsd) throws Exception {
		scanClasses("com.z_bank");
	}

	public static Set<Class<?>> scanClasses(String packageName) throws Exception{
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			String protocal = url.getProtocol();
			if ("file".equals(protocal)) {
				String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				cacheClassesOfPkg(packageName, filePath, true, classes);
			} else if ("jar".equals(protocal)) {
				// 获得jar文件
				JarFile jarFile = ((JarURLConnection)url.openConnection()).getJarFile();
				cacheClassOfJar(jarFile, packageDirName, classes);
			}
		}
		return classes;
	}

	public static void cacheClassOfJar(JarFile jarFile, String packageDirName, Set<Class<?>> classes) throws Exception {
		Enumeration<JarEntry> jarEntrys = jarFile.entries();
		while (jarEntrys.hasMoreElements()) {
			JarEntry jarEntry = jarEntrys.nextElement();
			String entryName = jarEntry.getName();
			System.out.println(entryName);
			if (entryName.charAt(0) == '/') {// 以/开头
				entryName = entryName.substring(1);
			}
			if (entryName.startsWith(packageDirName) && !entryName.contains("$")) {// 如果是以包名开头，且不是内部类
				if (entryName.endsWith(".class")) {
					String className = entryName.replace('/', '.');
					try {
						//classes.add(Class.forName(className.substring(0, className.length() - 6),false,Thread.currentThread().getContextClassLoader()));
						classes.add(Thread.currentThread().getContextClassLoader().loadClass(className.substring(0, className.length() - 6)));
					} catch (Exception e) {
						log.error(e);
						throw e;
					}
				}
			}
		}

	}

	public static void cacheClassesOfPkg(String pkgName, String pakPath, final boolean rescusive, Set<Class<?>> classes) throws Exception{
		File dir = new File(pakPath);
		if (!dir.isDirectory() || !dir.exists()) {
			return;
		}
		// 获取需要的目录和class文件
		File[] files = dir.listFiles(new FileFilter() {

			public boolean accept(File file) {
				return (rescusive && file.isDirectory()) || (file.getName().endsWith(".class")&&!file.getName().contains("$"));
			}
		});
		for (File file : files) {
			if (file.isDirectory()) {
				cacheClassesOfPkg(pkgName + "." + file.getName(), file.getAbsolutePath(), rescusive, classes);
			} else {
				String clsName = file.getName().substring(0, file.getName().length() - 6);
				try {
					classes.add(Thread.currentThread().getContextClassLoader().loadClass(pkgName + "." + clsName));
					//classes.add(Class.forName(pkgName+"."+clsName));//Class.forName会触发static代码，个人认为无关紧要
				} catch (Exception e) {
					log.error(e);
					throw e;
				}
			}
		}
	}
	
	
}
