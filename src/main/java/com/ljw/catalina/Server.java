package com.ljw.catalina;

import com.ljw.catalina.server.ServerProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Description tomcat服务
 * @Author Create by junwei.liang on 2020/7/2
 */
public class Server implements Lifecycle {
    private int port = 8080;

    private String address = "localhost";

    private boolean started = true;

    private Map<String, Service> serviceMap;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Service> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(Map<String, Service> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void init() {
        // 加载解析相关的配置，web.xml
        loadService();
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
    }

    /**
     * 加载应用
     */
    private void loadService() {

    }

    @Override
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("=====>>>Minicat start on port：" + port);

        while (isStarted()) {
            Socket socket = serverSocket.accept();
            ServerProcessor requestProcessor = new ServerProcessor(socket, serviceMap);
            threadPoolExecutor.execute(requestProcessor);
        }
    }
}
