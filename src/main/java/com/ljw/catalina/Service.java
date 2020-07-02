package com.ljw.catalina;

import java.util.Map;

/**
 * @Description 一个webapp应用
 * @Author Create by junwei.liang on 2020/7/2
 */
public class Service implements Lifecycle {
    private Map<String, HttpServlet> mapper;

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    public void doService(Request request, Response response) throws Exception {
        HttpServlet httpServlet = mapper.get(request.getUrl());
        if (httpServlet == null) {

        } else {
            httpServlet.service(request, response);
        }
    }
}
