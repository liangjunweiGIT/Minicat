package com.ljw.catalina.server;

import com.ljw.catalina.Request;
import com.ljw.catalina.Response;
import com.ljw.catalina.Service;
import com.ljw.catalina.util.StaticResourceUtil;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * @Description
 * @Author Create by junwei.liang on 2020/7/2
 */
public class ServerProcessor extends Thread {

    private Socket socket;
    private Map<String, Service> serviceMap;

    public ServerProcessor(Socket socket, Map<String, Service> serviceMap) {
        this.socket = socket;
        this.serviceMap = serviceMap;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            Service service = findService(request.getUrl());
            if (service != null) {
                service.doService(request, response);
            } else {
                response.outputHtml("/error.html");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Service findService(String url) {
        url = url.replace(StaticResourceUtil.CAT_PATH, "");
        final int i = url.indexOf("/");
        if (i != 0) {
            url = url.substring(0, i);
        }
        return serviceMap.get(url);
    }
}
