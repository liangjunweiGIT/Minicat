package com.ljw.catalina;

import com.ljw.catalina.loader.ServletClassLoader;
import com.ljw.catalina.util.StaticResourceUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description tomcat服务
 * @Author Create by junwei.liang on 2020/7/2
 */
public class Server implements Lifecycle {
    private int port = 8080;

    private String address = "localhost";

    private boolean started = true;

    private Map<String, Service> serviceMap;

    private String appBase;

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    private ThreadPoolExecutor threadPoolExecutor;

    private ServletClassLoader servletClassLoader;

    @Override
    public void init() throws Exception {

        loadConfig();

        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        serviceMap = new HashMap<>();
        loadService();
    }

    @Override
    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("=====>>>Minicat start on port：" + port);
        while (isStarted()) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, serviceMap);
            threadPoolExecutor.execute(requestProcessor);
        }
    }

    /**
     * 加载应用
     */
    private void loadService() throws Exception {
        String appPath = StaticResourceUtil.CAT_PATH + appBase + "/";
        String[] path = new File(appPath).list();
        if (path == null) {
            return;
        }
        for (String app : path) {
            String webConfig = appPath + app + "/web.xml";
            if (!new File(webConfig).exists()) {
                continue;
            }
            String name = app.replace(appPath, "");
            Service service = new Service(name, new ServletClassLoader(appPath + app), this);
            service.init();
            service.start();
            serviceMap.put(name, service);
        }
    }

    /**
     * 加载server.xml
     */
    private void loadConfig() throws Exception {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(resourceAsStream);
        Element rootElement = document.getRootElement();

        Element service = (Element) rootElement.selectSingleNode("Service");
        Element connector = (Element) service.selectSingleNode("Connector");
        this.port = Integer.parseInt(connector.attributeValue("port"));

        Element engine = (Element) connector.selectSingleNode("Engine");
        Element host = (Element) engine.selectSingleNode("Host");
        this.address = host.attributeValue("name");
        this.appBase = host.attributeValue("appBase");
    }
}
