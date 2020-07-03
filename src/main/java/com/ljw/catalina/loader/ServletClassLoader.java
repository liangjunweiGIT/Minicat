package com.ljw.catalina.loader;


import com.ljw.javax.servlet.Servlet;

import java.net.MalformedURLException;

/**
 * @Description
 * @Author Create by junwei.liang on 2020/7/2
 */
public class ServletClassLoader extends WebAppLoader {

    public ServletClassLoader(String path) throws MalformedURLException, NoSuchMethodException {
        super(path);
    }

    public Servlet loadServlet(String name) throws IllegalAccessException, InstantiationException {
        return (Servlet) super.findClass(name).newInstance();
    }

    /*public Class<?> loadClass(String className) throws ClassNotFoundException {

        Class<Servlet> clazz = (Class<Servlet>) classLoader.loadClass(className);
        return clazz.newInstance();
    }*/

    /*public static Set<Class<?>> loadClasses(String rootClassPath) throws Exception {
        Set<Class<?>> classSet = Sets.newHashSet();
        // 设置class文件所在根路径
        File clazzPath = new File(rootClassPath);

        // 记录加载.class文件的数量
        int clazzCount = 0;

        if (clazzPath.exists() && clazzPath.isDirectory()) {
            // 获取路径长度
            int clazzPathLen = clazzPath.getAbsolutePath().length() + 1;

            Stack<File> stack = new Stack<>();
            stack.push(clazzPath);

            // 遍历类路径
            while (!stack.isEmpty()) {
                File path = stack.pop();
                File[] classFiles = path.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        //只加载class文件
                        return pathname.isDirectory() || pathname.getName().endsWith(".class");
                    }
                });
                if (classFiles == null) {
                    break;
                }
                for (File subFile : classFiles) {
                    if (subFile.isDirectory()) {
                        stack.push(subFile);
                    } else {
                        if (clazzCount++ == 0) {
                            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                            boolean accessible = method.isAccessible();
                            try {
                                if (!accessible) {
                                    method.setAccessible(true);
                                }
                                // 设置类加载器
                                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                                // 将当前类路径加入到类加载器中
                                method.invoke(classLoader, clazzPath.toURI().toURL());
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                method.setAccessible(accessible);
                            }
                        }
                        // 文件名称
                        String className = subFile.getAbsolutePath();
                        className = className.substring(clazzPathLen, className.length() - 6);
                        //将/替换成. 得到全路径类名
                        className = className.replace(File.separatorChar, '.');
                        // 加载Class类
                        Class<?> aClass = Class.forName(className);
                        classSet.add(aClass);
                        System.out.println("读取应用程序类文件[class={" + className + "}]");
                    }
                }
            }
        }
        return classSet;
    }*/

}
