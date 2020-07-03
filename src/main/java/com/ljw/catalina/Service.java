package com.ljw.catalina;

import com.ljw.catalina.loader.ServletClassLoader;
import com.ljw.catalina.util.StaticResourceUtil;
import com.ljw.javax.servlet.Request;
import com.ljw.javax.servlet.Response;
import com.ljw.javax.servlet.Servlet;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 一个webapp应用
 * @Author Create by junwei.liang on 2020/7/2
 */
public class Service implements Lifecycle {
    private Map<String, Servlet> mapper;

    private String name;

    private Server server;

    private ServletClassLoader servletClassLoader;

    Service(String name, ServletClassLoader servletClassLoader, Server server) {
        this.servletClassLoader = servletClassLoader;
        this.server = server;
        this.name = name;
    }

    @Override
    public void init() throws Exception {
        mapper = new HashMap<>();
        loadServlet();
    }

    @Override
    public void start() throws Exception {

    }

    public void doService(Request request, Response response) throws Exception {
        Servlet servlet = mapper.get(request.getUrl());
        if (servlet == null) {
            response.outputHtml(server.getAppBase() + "/" + name + "/error.html");
        } else {
            servlet.service(request, response);
        }
    }

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private void loadServlet() throws Exception {

        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(new File(StaticResourceUtil.CAT_PATH + server.getAppBase() + "/" + name + "/web.xml"));
        Element rootElement = document.getRootElement();

        List<Element> selectNodes = rootElement.selectNodes("//servlet");
        for (Element element : selectNodes) {
            // <servlet-name>lagou</servlet-name>
            Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
            String servletName = servletnameElement.getStringValue();
            // <servlet-class>server.LagouServlet</servlet-class>
            Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
            String servletClass = servletclassElement.getStringValue();

            // 根据servlet-name的值找到url-pattern
            Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
            // /lagou
            String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
            mapper.put("/" + name + urlPattern, servletClassLoader.loadServlet(servletClass));
        }

    }
}
