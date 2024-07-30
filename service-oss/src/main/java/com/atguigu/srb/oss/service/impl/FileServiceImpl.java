package com.atguigu.srb.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.atguigu.srb.oss.service.FileService;
import com.atguigu.srb.oss.util.OSSProperties;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @Author:zxp
 * @Description:
 * @Date:13:24 2024/2/26
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        String endpoint = OSSProperties.ENDPOINT;
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
//        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "examplebucket";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "exampledir/exampleobject.txt";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "D:\\localpath\\examplefile.txt";

        String accessKeyId= OSSProperties.KEY_ID;
        String accessKeySecret=OSSProperties.KEY_SECRET;
        String bucketName = OSSProperties.BUCKET_NAME;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret );
        if(!ossClient.doesBucketExist(bucketName)){
            ossClient.createBucket(bucketName);
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }
        else
            ossClient.setBucketAcl(bucketName,CannedAccessControlList.PublicRead);
        String timeFolder = new DateTime().toString("yyyy/MM/dd");
        String s = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf('.'));
        String key=module+"/"+timeFolder+"/"+s;
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream);
        ossClient.putObject(bucketName,key,inputStream);
        ossClient.shutdown();
        return "https://" + OSSProperties.BUCKET_NAME + "." + OSSProperties.ENDPOINT + "/" + key;
    }

    @Override
    public void removeFile(String url) {
        String endpoint = OSSProperties.ENDPOINT;
        String accessKeyId= OSSProperties.KEY_ID;
        String accessKeySecret=OSSProperties.KEY_SECRET;
        String bucketName = OSSProperties.BUCKET_NAME;
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret );
        String host="https://"+bucketName+"."+endpoint+"/";
        String objectName=url.substring(host.length());
        ossClient.deleteObject(bucketName,objectName);
    }
}
