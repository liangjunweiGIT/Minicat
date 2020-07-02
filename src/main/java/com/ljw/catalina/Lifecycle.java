package com.ljw.catalina;

import java.io.IOException;

/**
 * @Description
 * @Author Create by junwei.liang on 2020/7/2
 */
public interface Lifecycle {
    void init() throws Exception;

    void start() throws Exception;
}
