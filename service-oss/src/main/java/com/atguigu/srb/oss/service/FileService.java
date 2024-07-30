package com.atguigu.srb.oss.service;

import java.io.InputStream;

/**
 * @Author:zxp
 * @Description:
 * @Date:13:23 2024/2/26
 */
public interface FileService {
    String upload(InputStream inputStream, String module, String fileName);

    void removeFile(String url);
}
