package com.ljw.catalina.startup;

import com.ljw.catalina.Lifecycle;
import com.ljw.catalina.Server;

import java.io.IOException;

/**
 * Minicat的主类
 */
public final class Bootstrap {

    /**
     * Minicat启动需要初始化展开的一些操作
     */
    public void start() throws Exception {
        Lifecycle server = new Server();
        server.init();
        server.start();
    }


    /**
     * Minicat 的程序启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
